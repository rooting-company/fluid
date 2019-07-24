package br.com.rooting.liquid.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.stream.Stream;

public final class ReflectionUtils {

    private ReflectionUtils() {}

    public static boolean isFinalType(Field f) {
        return ReflectionUtils.isFinalType(f.getType());
    }

    public static boolean isFinalType(Class<?> type) {
        try {
            FinalType.getFindType(type);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public static <T> T getInstance(Class<T> type) throws IllegalAccessException, InstantiationException {
        return type.newInstance();
    }

}