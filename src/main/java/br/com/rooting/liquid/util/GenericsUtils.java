package br.com.rooting.liquid.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public final class GenericsUtils {

    private GenericsUtils() {}

    public static Class<?> getParametizedClass(Field field) {
        if(field.getGenericType() instanceof ParameterizedType)  {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            return (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }
        return Object.class;
    }

    public static boolean isParameterizedTypeCompatible(Class<?> type, Class<?> targetType) {
//        if(type.getGenericInterfaces()[0] instanceof ParameterizedType)  {
        return true;
    }

}
