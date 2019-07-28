package br.com.rooting.liquid.converter.exception;

import br.com.rooting.liquid.converter.LiquifyConverter;

public class FailToConvertToLiquidException extends RuntimeException {

    public FailToConvertToLiquidException(LiquifyConverter<?> converter, String key, Exception e) {
        super(formatMessage(converter, key), e);
    }

    private static String formatMessage(LiquifyConverter<?> converter, String key) {
        StringBuilder builder = new StringBuilder("Error when processing converter \"");
        builder.append(converter.getClass());
        builder.append("\", for property \"");
        builder.append(key);
        return builder.toString();
    }

}
