package br.com.rooting.liquid.core.solid;

import br.com.rooting.liquid.config.Config;
import br.com.rooting.liquid.converter.*;
import br.com.rooting.liquid.converter.annotation.Converter;
import br.com.rooting.liquid.converter.exception.FailToConvertToSolidException;
import br.com.rooting.liquid.core.KeyBuilder;
import br.com.rooting.liquid.core.solid.exception.FailToSolidifyException;
import br.com.rooting.liquid.core.solid.exception.NotFilledObjectException;
import br.com.rooting.liquid.core.solid.tracker.Tracker;
import br.com.rooting.liquid.mapping.Ignore;
import br.com.rooting.liquid.model.LiquidObject;
import br.com.rooting.liquid.model.LiquidProperty;
import br.com.rooting.liquid.util.FinalType;
import br.com.rooting.liquid.util.GenericsUtils;
import br.com.rooting.liquid.util.ReflectionUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static br.com.rooting.liquid.util.FinalType.getFinalType;
import static br.com.rooting.liquid.util.ReflectionUtils.isFinalType;

public class Solidifier<T> {

    private Logger logger = Logger.getLogger(this.getClass());

    private LiquidObject liquidObject;

    private KeyBuilder keyBuilder;

    private T object;

    private Tracker tracker;

    private ConverterRepository converterRepository;

    private List<Extractivist> extractivists;

    public Solidifier(T object, LiquidObject liquidObject, Config config) {
        this.liquidObject = liquidObject;
        this.keyBuilder = new KeyBuilder(object);
        this.object = object;
        this.tracker = new Tracker();
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

    public Solidifier(Class<T> type, LiquidObject liquidObject, Config config) {
        this(createInstance(type), liquidObject, config);
    }

    public T solidify() {
        try {
            return this.loadProperties(this.object);

        } catch (NotFilledObjectException e) {
            return this.object;

        } finally {
            this.destroy();
        }
    }

    private void destroy() {
        this.liquidObject = null;
        this.keyBuilder = null;
        this.object = null;
        this.tracker = null;
        this.converterRepository = null;
        this.extractivists = null;
    }

    private <Z> Z loadProperties(Z target) {
        Field[] fields = target.getClass().getDeclaredFields();
        boolean filled = false;

        this.tracker.track(target);
        try {

            for (Field f : fields) {
                if (this.isIgnored(f))
                    continue;

                f.setAccessible(true);
                this.keyBuilder.next(f);

                try {
                    Object value = this.getValueFromField(f, target);
                    // TODO implamentar o replace value
                    if (value != null)
                        continue;

                    Class<?> fieldType = f.getType();
                    SolidifyConverter<Object> converter = this.getCustomConverter(f);
                    if (this.wasExtractedWithConverter(target, value, f, converter)) {
                        continue;
                    }

                    for (Extractivist extractor : this.extractivists) {
                        if (extractor.isCompatible(fieldType)) {
                            value = extractor.extract(value, f, converter);
                            break;
                        }
                    }

                    if (value != null) {
                        this.load(target, value, f);
                        filled = true;
                    }

                } finally {
                    this.keyBuilder.previous();
                }
            }

            if (!filled)
                throw new NotFilledObjectException();

        } finally {
            this.tracker.untrack(target);
        }

        return target;
    }

    private SolidifyConverter<Object> getCustomConverter(Field f) {
        Converter converter = f.getAnnotation(Converter.class);
        if (converter != null &&
                !SolidifyConverter.class.equals(converter.solidify())) {
            return this.converterRepository.getSolidifyConverter(converter.solidify(), f.getType());
        }
        return null;
    }

    private boolean wasExtractedWithConverter(Object target, Object value,
                                              Field f, SolidifyConverter<Object> converter) {

        if (converter != null && !Collection.class.isAssignableFrom(f.getType())) {
            this.convert(value, converter);
            this.load(target, value, f);
            return true;
        }
        return false;
    }

    private Object convert(Object value, SolidifyConverter<Object> converter) {
        String key = this.keyBuilder.build();
        try {
            return converter.convertToSolid(value, key, this.liquidObject);
        } catch (Exception e) {
            throw new FailToConvertToSolidException(converter, key, e);
        }
    }

    private Object getValueFromField(Field f, Object target) {
        try {
            return f.get(target);

        } catch (IllegalAccessException e) {
            throw new FailToSolidifyException(e);
        }
    }

    private boolean isIgnored(Field f) {
        return f.isAnnotationPresent(Ignore.class);
    }

    private void load(Object target, Object value, Field field) {
        if (value != null) {
            try {
                field.set(target, value);

            } catch (IllegalAccessException e) {
                throw new FailToSolidifyException(e);
            }
        }
    }

    private LiquidProperty findLiquidProperty() {
        return this.liquidObject.findProperty(keyBuilder.build());
    }

    private static <P> P createInstance(Class<P> type) {
        try {
            return ReflectionUtils.getInstance(type);

        } catch (IllegalAccessException | InstantiationException e) {
            throw new FailToSolidifyException(e);
        }
    }

    private interface Extractivist {
        boolean isCompatible(Class<?> type);

        Object extract(Object currentValue, Field f, SolidifyConverter<Object> converter);
    }

    private class FinalTypeExtractor implements Extractivist {

        @Override
        public boolean isCompatible(Class<?> type) {
            return isFinalType(type);
        }

        @Override
        public Object extract(Object currentValue, Field f, SolidifyConverter<Object> converter) {
            FinalType finalType = getFinalType(f.getType());
            LiquidProperty property = findLiquidProperty();

            if (property != null) {
                return finalType.formatAsObject(property.getValue());
            }
            return null;
        }

    }

    private class EnumExtractor implements Extractivist {

        @Override
        public boolean isCompatible(Class<?> type) {
            return type.isEnum();
        }

        @Override
        public Object extract(Object currentValue, Field f, SolidifyConverter<Object> converter) {
            LiquidProperty property = findLiquidProperty();
            if (property != null) {
                try {
                    return Enum.valueOf((Class<Enum>) f.getType(), property.getValue());
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid enum value for the property:" + property, e);
                }
            }
            return null;
        }
    }

    private class ListExtractor implements Extractivist {

        @Override
        public boolean isCompatible(Class<?> type) {
            return List.class.isAssignableFrom(type);
        }

        @Override
        public Object extract(Object currentValue, Field f, SolidifyConverter<Object> converter) {
            keyBuilder.nextOrder();

            try {
                Class<?> listType = GenericsUtils.getParametizedClass(f);
                List<Object> list = new ArrayList<>();
                boolean hasNextValue = true;
                do {
                    Object listItem = this.getListItemValue(listType, converter);

                    if (listItem != null) {
                        list.add(listItem);
                        keyBuilder.incrementOrder();
                        continue;
                    }

                    hasNextValue = false;
                } while (hasNextValue);

                if (!list.isEmpty()) {
                    return list;
                }

            } finally {
                Solidifier.this.keyBuilder.previousOrder();
            }
            return null;
        }

        private Object getListItemValue(Class<?> listType, SolidifyConverter<Object> converter) {
            if (converter != null) {
                return convert(null, converter);
            }

            try {
                Object item = createInstance(listType);
                return loadProperties(item);

            } catch (NotFilledObjectException e) {
                return null;
            }
        }
    }

    private class ObjectExtractor implements Extractivist {

        @Override
        public boolean isCompatible(Class<?> type) {
            return true;
        }

        @Override
        public Object extract(Object currentValue, Field f, SolidifyConverter<Object> converter) {
            try {
                return loadProperties(createInstance(f.getType()));

            } catch (NotFilledObjectException e) {
                return null;
            }
        }
    }

}
