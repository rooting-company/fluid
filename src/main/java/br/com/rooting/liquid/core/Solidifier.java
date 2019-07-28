package br.com.rooting.liquid.core;

import br.com.rooting.liquid.config.Config;
import br.com.rooting.liquid.converter.*;
import br.com.rooting.liquid.converter.annotation.Converter;
import br.com.rooting.liquid.converter.exception.FailToConvertToSolidException;
import br.com.rooting.liquid.mapping.Ignore;
import br.com.rooting.liquid.result.LiquidObject;
import br.com.rooting.liquid.result.LiquidProperty;
import br.com.rooting.liquid.util.FinalType;
import br.com.rooting.liquid.util.GenericsUtils;
import br.com.rooting.liquid.util.ReflectionUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class Solidifier<T> {

    private Logger logger = Logger.getLogger(this.getClass());

    private LiquidObject liquidObject;

    private KeyBuilder keyBuilder;

    private T object;

    private Tracker tracker;

    private ConverterRepository converterRepository;

    Solidifier(T object, LiquidObject liquidObject, Config config) {
        this.liquidObject = liquidObject;
        this.keyBuilder = new KeyBuilder(object);
        this.object = object;
        this.tracker = new Tracker();
        this.converterRepository = config.getConverterRepository();
    }

    Solidifier(Class<T> type, LiquidObject liquidObject, Config config) {
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
                    if (converter != null && !Collection.class.isAssignableFrom(fieldType)) {
                        this.loadWithCustomConverter(target, f, value, converter);
                        continue;
                    }

                    if (ReflectionUtils.isFinalType(fieldType)) {
                        value = this.getSimplePropertyValue(fieldType);

                    } else if (fieldType.isEnum()) {
                        value = this.getEnumPropertyValue((Class<Enum>) fieldType);

                    } else if (List.class.isAssignableFrom(fieldType)) {
                        value = this.getListPropertyValue(f, converter);

                    } else {
                        value = this.getComplexPropertyValue(fieldType);
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

    private void loadWithCustomConverter(Object target,
                                           Field field,
                                           Object currentValue,
                                           SolidifyConverter<Object> converter) {
        String key = this.keyBuilder.build();
        try {
            currentValue = converter.convertToSolid(currentValue, key, this.liquidObject);
        } catch (Exception e) {
            throw new FailToConvertToSolidException(converter, key, e);
        }
        this.load(target, currentValue, field);
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

    private Object getSimplePropertyValue(Class<?> type) {
        FinalType finalType = FinalType.getFindType(type);
        LiquidProperty property = this.findLiquidProperty();

        if (property != null) {
            return finalType.formatAsObject(property.getValue());
        }
        return null;
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

    private Object getComplexPropertyValue(Class<?> type) {
        try {
            return this.loadProperties(createInstance(type));

        } catch (NotFilledObjectException e) {
            return null;
        }
    }

    private <E extends Enum<E>> E getEnumPropertyValue(Class<E> type) {
        LiquidProperty property = this.findLiquidProperty();
        if (property != null) {
            try {
                return Enum.valueOf(type, property.getValue());
            } catch (IllegalArgumentException e) {
                logger.error("Invalid enum value for the property:" + property, e);
            }
        }
        return null;
    }

    // TODO Da suporte a listas ja existentes
    private List<?> getListPropertyValue(Field field, SolidifyConverter<Object> converter) {
        this.keyBuilder.nextOrder();

        try {
            Class<?> listType = GenericsUtils.getParametizedClass(field);
            List<Object> list = new ArrayList<>();
            boolean hasNextValue = true;
            do {
                Object listItem = this.getListItemValue(listType, converter);

                if (listItem != null) {
                    list.add(listItem);
                    this.keyBuilder.incrementOrder();
                    continue;
                }

                hasNextValue = false;
            } while (hasNextValue);

            if (!list.isEmpty()) {
                return list;
            }

        } finally {
            this.keyBuilder.previousOrder();
        }

        return null;
    }

    private Object getListItemValue(Class<?> listType, SolidifyConverter<Object> converter) {
        if (converter != null) {
            return converter.convertToSolid(null, this.keyBuilder.build(), this.liquidObject);
        }

        try {
            Object item = createInstance(listType);
            return this.loadProperties(item);

        } catch (NotFilledObjectException e) {
            return null;
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

}
