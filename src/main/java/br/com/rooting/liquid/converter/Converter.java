package br.com.rooting.liquid.converter;

public @interface Converter {

    Class<? extends LiquifyConverter> liquifyConverter();

    Class<? extends SolidifyConverter> solidifyConverter();

}
