package br.com.rooting.liquid.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

final class TemporalUtils {

    private TemporalUtils() {}

    static LocalDate formatAsLocalDate(String value) {
        return LocalDate.parse(value, DateTimeFormatter.ISO_DATE);
    }

    static LocalDateTime formatAsLocalDateTime(String value) {
        return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
    }

    static String formatLocalDateAsString(Object object) {
        return ((LocalDate) object).format(DateTimeFormatter.ISO_DATE);
    }

    static String formatLocalDateTimeAsString(Object object) {
        return ((LocalDateTime) object).format(DateTimeFormatter.ISO_DATE_TIME);
    }

}