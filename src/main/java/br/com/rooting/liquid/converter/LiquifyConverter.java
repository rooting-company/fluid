package br.com.rooting.liquid.converter;

import br.com.rooting.liquid.model.LiquidProperty;

import java.lang.reflect.Field;
import java.util.List;

public interface LiquifyConverter<T> {

    List<LiquidProperty> convertToLiquid(Field field, String key, T current);

}
