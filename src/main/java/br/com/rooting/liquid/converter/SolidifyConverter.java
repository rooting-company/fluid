package br.com.rooting.liquid.converter;

import br.com.rooting.liquid.model.LiquidObject;

public interface SolidifyConverter<T> {

    T convertToSolid(T current, String key, LiquidObject liquidObject);

}
