package br.com.rooting.liquid.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.stream.Stream;

public enum FinalType {
    STRING(String.class, String::toString),
    BOOLEAN_WRAPPER(Boolean.class, Boolean::valueOf),
    INTEGER_WRAPPER(Integer.class, Integer::valueOf),
    LONG_WRAPPER(Long.class, Long::valueOf),
    DOUBLE_WRAPPER(Double.class, Double::valueOf),
    FLOAT_WRAPPER(Float.class, Float::valueOf),
    BIG_DECIMAL(BigDecimal.class, BigDecimal::new),
    BOOLEAN(boolean.class, Boolean::parseBoolean),
    INTEGER(int.class, Integer::parseInt),
    LONG(long.class, Long::parseLong),
    DOUBLE(double.class, Double::parseDouble),
    FLOAT(float.class, Float::parseFloat),
    LOCAL_DATE(LocalDate.class, TemporalUtils::formatAsLocalDate, TemporalUtils::formatLocalDateAsString),
    LOCAL_DATE_TIME(LocalDateTime.class, TemporalUtils::formatAsLocalDateTime, TemporalUtils::formatLocalDateTimeAsString);

    private final Class<?> type;

    private final Function<String,?> formatObjectFunction;

    private final Function<Object, String> formatStringFunction;

    <T> FinalType(Class<T> type, Function<String, T> formatObjectFunction) {
        this.type = type;
        this.formatObjectFunction = formatObjectFunction;
        this.formatStringFunction = null;
    }

    <T> FinalType(Class<T> type,
                  Function<String, T> formatObjectFunction,
                  Function<Object, String> formatStringFunction) {

        this.type = type;
        this.formatObjectFunction = formatObjectFunction;
        this.formatStringFunction = formatStringFunction;
    }

    public boolean hasTheSameType(Class<?> type) {
        return this.type.equals(type);
    }

    public Object formatAsObject(String value) {
        return this.formatObjectFunction.apply(value);
    }

    public String formatAsString(Object object) {
        if (this.formatStringFunction == null)
            return object.toString();

        return this.formatStringFunction.apply(object);
    }

    public static FinalType getFinalType(Class<?> type) {
        return Stream.of(FinalType.values())
                        .filter(finalType -> finalType.hasTheSameType(type))
                        .findFirst()
                        .orElseThrow(IllegalArgumentException::new);
    }

}