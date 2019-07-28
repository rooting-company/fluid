package br.com.rooting.liquid.core;

import br.com.rooting.liquid.config.Config;
import br.com.rooting.liquid.converter.annotation.Converter;
import br.com.rooting.liquid.converter.ConverterRepository;
import br.com.rooting.liquid.converter.LiquifyConverter;
import br.com.rooting.liquid.converter.exception.FailToConvertToLiquidException;
import br.com.rooting.liquid.mapping.Ignore;
import br.com.rooting.liquid.result.LiquidObject;
import br.com.rooting.liquid.result.LiquidProperty;
import br.com.rooting.liquid.util.FinalType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static br.com.rooting.liquid.util.FinalType.getFindType;
import static br.com.rooting.liquid.util.ReflectionUtils.isFinalType;

class Liquifier {

    private List<LiquidProperty> properties;

    private KeyBuilder keyBuilder;

    private Object object;

    private ConverterRepository converterRepository;

    Liquifier(Object object, Config config) {
        this.properties = new ArrayList<>();
        this.keyBuilder = new KeyBuilder(object);
        this.object = object;
        this.converterRepository = config.getConverterRepository();
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
    }

    private void extractProperties(Object target) {
        Field[] fields = target.getClass().getDeclaredFields();

        for (Field f : fields) {
            if(this.isIgnored(f))
                continue;

            f.setAccessible(true);
            this.keyBuilder.next(f);

            try {
                Object propertyValue = this.getValueFromField(f, target);
                // TODO implamentar o replace value
                if (propertyValue == null)
                    continue;

                LiquifyConverter<Object> converter = this.getCustomConverter(f);
                if (converter != null && !Collection.class.isAssignableFrom(f.getType())) {
                    this.extracWithCustomConverter(f, propertyValue, converter);
                    continue;
                }

                if (isFinalType(f)) {
                    FinalType finalType = getFindType(f.getType());
                    this.extractFromFinalType(finalType, propertyValue);

                } else if (Enum.class.isAssignableFrom(object.getClass())) {
                    this.extractFromEnum((Enum<?>) object);

                } else if (List.class.isAssignableFrom(f.getType())) {
                    this.extractFromList((List<?>) propertyValue, f, converter);

                } else {
                    this.extractFromObject(propertyValue);
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

    private void extracWithCustomConverter(Field field, Object propertyValue, LiquifyConverter<Object> converter) {
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

    private void extractFromFinalType(FinalType finalType, Object valueProperty) {
        String value = finalType.formatAsString(valueProperty);
        this.addProperty(value);
    }

    private void extractFromObject(Object object) {
        this.extractProperties(object);
    }

    private void extractFromEnum(Enum<?> enumObject) {
        this.addProperty(enumObject.name());
    }

    private void addProperty(String value) {
        LiquidProperty p = new LiquidProperty(this.keyBuilder.build(), value);
        this.properties.add(p);
    }

    private void extractFromList(List<?> list, Field field, LiquifyConverter<Object> converter) {
        this.keyBuilder.nextOrder();
        try {
            list.forEach(value -> {
                if (converter != null) {
                    this.extracWithCustomConverter(field, value, converter);
                } else {
                    this.extractProperties(value);
                }
                this.keyBuilder.incrementOrder();
            });

        } finally {
            this.keyBuilder.previousOrder();
        }
    }

    private Object getValueFromField(Field f, Object target) {
        try {
            return f.get(target);

        } catch (IllegalAccessException e) {
            throw new FailToLiquifyException(e);
        }
    }

}
