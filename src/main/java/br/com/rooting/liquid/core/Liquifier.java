package br.com.rooting.liquid.core;

import br.com.rooting.liquid.result.LiquidObject;
import br.com.rooting.liquid.result.LiquidProperty;
import br.com.rooting.liquid.util.FinalType;
import br.com.rooting.liquid.util.GenericsUtils;
import jdk.nashorn.internal.ir.annotations.Ignore;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static br.com.rooting.liquid.util.FinalType.getFindType;
import static br.com.rooting.liquid.util.ReflectionUtils.isFinalType;

class Liquifier {

    private List<LiquidProperty> properties;

    private KeyBuilder keyBuilder;

    private Object object;

    Liquifier(Object object) {
        this.properties = new ArrayList<>();
        this.keyBuilder = new KeyBuilder(object);
        this.object = object;
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
                if (propertyValue == null)
                    continue;

                if (isFinalType(f)) {
                    FinalType finalType = getFindType(f.getType());
                    this.extractFromFinalType(finalType, propertyValue);

                } else if (List.class.isAssignableFrom(f.getType())) {
                    this.extractFromList(f, (List<?>) propertyValue);

                } else {
                    this.extractFromObject(propertyValue);
                }
            } finally {
                this.keyBuilder.previous();
            }
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
        if (Enum.class.isAssignableFrom(object.getClass())) {
            this.extractFromEnum((Enum<?>) object);
            return;
        }

        this.extractProperties(object);
    }

    private void extractFromEnum(Enum<?> enumObject) {
        this.addProperty(enumObject.name());
    }

    private void addProperty(String value) {
        LiquidProperty p = new LiquidProperty(this.keyBuilder.build(), value);
        this.properties.add(p);
    }

    private void extractFromList(Field field, List<?> list) {
        this.keyBuilder.nextOrder();

        try {
            Class<?> listType = GenericsUtils.getParametizedClass(field);
            boolean isSimpleProperty = isFinalType(listType);

            for (Object value : list) {
                if (!isSimpleProperty) {
                    this.extractFromObject(value);

                } else {
                    FinalType finalType = getFindType(listType);
                    this.extractFromFinalType(finalType, value);
                }
                this.keyBuilder.incrementOrder();
            }

        } finally {
            this.keyBuilder.previousOrder();
        }
    }

    private Object getValueFromField(Field f, Object target) {
        try {
            return f.get(target);

        } catch (IllegalAccessException e) {
            throw new FailToSolidifyException(e);
        }
    }

}
