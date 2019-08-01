package br.com.rooting.liquid.core.liquid;

import br.com.rooting.liquid.config.Config;
import br.com.rooting.liquid.converter.ConverterRepository;
import br.com.rooting.liquid.converter.LiquifyConverter;
import br.com.rooting.liquid.converter.annotation.Converter;
import br.com.rooting.liquid.converter.exception.FailToConvertToLiquidException;
import br.com.rooting.liquid.core.KeyBuilder;
import br.com.rooting.liquid.core.liquid.exception.FailToLiquifyException;
import br.com.rooting.liquid.mapping.Ignore;
import br.com.rooting.liquid.model.LiquidObject;
import br.com.rooting.liquid.model.LiquidProperty;
import br.com.rooting.liquid.util.FinalType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static br.com.rooting.liquid.util.FinalType.getFinalType;
import static br.com.rooting.liquid.util.ReflectionUtils.isFinalType;

public class Liquifier {

    private List<LiquidProperty> properties;

    private KeyBuilder keyBuilder;

    private Object object;

    private ConverterRepository converterRepository;

    private List<Extractivist> extractivists;

    public Liquifier(Object object, Config config) {
        this.properties = new ArrayList<>();
        this.keyBuilder = new KeyBuilder(object);
        this.object = object;
        this.converterRepository = config.getConverterRepository();
        this.initExtractivists();
    }

    private void initExtractivists() {
        this.extractivists = new ArrayList<>();
        this.extractivists.add(new FinalTypeExtractor());
        this.extractivists.add(new EnumExtractor());
        this.extractivists.add(new ListExtractor());
        this.extractivists.add(new ObjectExtractor());
    }

    public LiquidObject liquify() {
        try {
            this.extractProperties(this.object);
            return LiquidObject.create(this.properties);

        } finally {
            this.destroy();
        }
    }

    private void destroy() {
        this.properties = null;
        this.keyBuilder = null;
        this.object = null;
        this.converterRepository = null;
        this.extractivists = null;
    }

    private void extractProperties(Object target) {
        Field[] fields = target.getClass().getDeclaredFields();

        for (Field f : fields) {
            if(this.isIgnored(f))
                continue;

            f.setAccessible(true);
            this.keyBuilder.next(f);

            try {
                Object value = this.getValueFromField(f, target);
                // TODO implamentar o extract null value
                if (value == null)
                    continue;

                Class<?> fieldType = f.getType();
                LiquifyConverter<Object> converter = this.getCustomConverter(f);
                if (this.wasExtractedWithConverter(value, f, converter)) {
                    continue;
                }

                for (Extractivist extractor : this.extractivists) {
                    if (extractor.isCompatible(fieldType)) {
                        extractor.extract(value, f, converter);
                        break;
                    }
                }

            } finally {
                this.keyBuilder.previous();
            }
        }
    }

    private LiquifyConverter<Object> getCustomConverter(Field f) {
        Converter converter = f.getAnnotation(Converter.class);
        if (converter != null
                && !LiquifyConverter.class.equals(converter.liquify())) {
            return this.converterRepository.getLiquifyConverter(converter.liquify(), f.getType());
        }
        return null;
    }

    private boolean wasExtractedWithConverter(Object value, Field f, LiquifyConverter<Object> converter) {
        if (converter != null && !Collection.class.isAssignableFrom(f.getType())) {
            this.convert(f, value, converter);
            return true;
        }
        return false;
    }

    private void convert(Field field, Object propertyValue, LiquifyConverter<Object> converter) {
        String key = this.keyBuilder.build();
        try {
            List<LiquidProperty> properties = converter.convertToLiquid(field, key, propertyValue);
            if (properties != null)
                properties.forEach(this.properties::add);

        } catch (Exception e) {
            throw new FailToConvertToLiquidException(converter, key, e);
        }
    }

    private boolean isIgnored(Field f) {
        return f.isAnnotationPresent(Ignore.class);
    }

    private void addProperty(String value) {
        LiquidProperty p = new LiquidProperty(this.keyBuilder.build(), value);
        this.properties.add(p);
    }

    private Object getValueFromField(Field f, Object target) {
        try {
            return f.get(target);

        } catch (IllegalAccessException e) {
            throw new FailToLiquifyException(e);
        }
    }

    private interface Extractivist {
        boolean isCompatible(Class<?> type);

        void extract(Object currentValue, Field f, LiquifyConverter<Object> converter);
    }

    private class FinalTypeExtractor implements Extractivist {

        @Override
        public boolean isCompatible(Class<?> type) {
            return isFinalType(type);
        }

        @Override
        public void extract(Object currentValue, Field f, LiquifyConverter<Object> converter) {
            FinalType finalType = getFinalType(f.getType());
            String value = finalType.formatAsString(currentValue);
            addProperty(value);
        }

    }

    private class EnumExtractor implements Extractivist {

        @Override
        public boolean isCompatible(Class<?> type) {
            return type.isEnum();
        }

        @Override
        public void extract(Object currentValue, Field f, LiquifyConverter<Object> converter) {
            Enum<?> enumValue = (Enum<?>) currentValue;
            addProperty(enumValue.name());
        }

    }

    private class ListExtractor implements Extractivist {

        @Override
        public boolean isCompatible(Class<?> type) {
            return List.class.isAssignableFrom(type);
        }

        @Override
        public void extract(Object currentValue, Field f, LiquifyConverter<Object> converter) {
            keyBuilder.nextOrder();
            try {
                List<?> list = (List<?>) currentValue;
                list.forEach(value -> {
                    if (converter != null) {
                        convert(f, value, converter);
                    } else {
                        extractProperties(value);
                    }
                    keyBuilder.incrementOrder();
                });

            } finally {
                keyBuilder.previousOrder();
            }
        }

    }

    private class ObjectExtractor implements Extractivist {

        @Override
        public boolean isCompatible(Class<?> type) {
            return true;
        }

        @Override
        public void extract(Object currentValue, Field f, LiquifyConverter<Object> converter) {
            extractProperties(object);
        }
    }

}
