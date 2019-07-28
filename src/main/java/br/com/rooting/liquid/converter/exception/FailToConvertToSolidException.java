package br.com.rooting.liquid.converter.exception;

import br.com.rooting.liquid.converter.SolidifyConverter;

public class FailToConvertToSolidException extends RuntimeException {

    public FailToConvertToSolidException(SolidifyConverter<Object> converter, String key, Exception e) {
        super(formatMessage(converter, key), e);
    }

    private static String formatMessage(SolidifyConverter<?> converter, String key) {
        StringBuilder builder = new StringBuilder("Error when processing converter \"");
        builder.append(converter.getClass());
        builder.append("\", for property \"");
        builder.append(key);
        return builder.toString();
    }

}
