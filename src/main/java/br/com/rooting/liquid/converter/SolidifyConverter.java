package br.com.rooting.liquid.converter;

import br.com.rooting.liquid.result.LiquidProperty;

import java.util.Map;

public interface SolidifyConverter<T> {

    T converter(T current, String key, Map<String, LiquidProperty> properties);

}
