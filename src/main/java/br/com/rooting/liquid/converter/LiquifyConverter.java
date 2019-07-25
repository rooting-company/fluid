package br.com.rooting.liquid.converter;

import br.com.rooting.liquid.result.LiquidProperty;

import java.util.List;

public interface LiquifyConverter<T> {

    List<LiquidProperty> convert(T current);

}
