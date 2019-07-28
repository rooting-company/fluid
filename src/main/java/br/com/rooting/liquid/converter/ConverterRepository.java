package br.com.rooting.liquid.converter;

import java.util.List;
import java.util.Map;

import static br.com.rooting.liquid.util.GenericsUtils.isParameterizedTypeCompatible;
import static br.com.rooting.liquid.util.ReflectionUtils.getInstance;
import static java.util.stream.Collectors.toMap;

public class ConverterRepository {

    private final Map<Class<?>, LiquifyConverter<?>> liquifyConverters;

    private final Map<Class<?>, SolidifyConverter<?>> solidifyConverters;

    public ConverterRepository(List<LiquifyConverter<?>> liquifyConverters,
                               List<SolidifyConverter<?>> solidifyConverters) {

        this.liquifyConverters = liquifyConverters.stream()
                                                  .collect(toMap(LiquifyConverter::getClass, c -> c));
        this.solidifyConverters = solidifyConverters.stream()
                                                    .collect(toMap(SolidifyConverter::getClass, c -> c));
    }

    public <Z extends LiquifyConverter<?>> Z getLiquifyConverter(Class<Z> liquifyConverterType, Class<?> targetType) {
        this.validConverterType(liquifyConverterType, targetType);

        Z converter = (Z) this.liquifyConverters.get(liquifyConverterType);

        if (converter == null) {
            try {
                converter = getInstance(liquifyConverterType);
                this.liquifyConverters.put(converter.getClass(), converter);

            } catch (IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
        return converter;
    }

    public <Z extends SolidifyConverter<?>> Z getSolidifyConverter(Class<Z> solidifyConverterType, Class<?> targetType) {
        this.validConverterType(solidifyConverterType, targetType);

        Z converter = (Z) this.solidifyConverters.get(solidifyConverterType);

        if (converter == null) {
            try {
                converter = getInstance(solidifyConverterType);
                this.solidifyConverters.put(converter.getClass(), converter);

            } catch (IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
        return converter;
    }

    private void validConverterType(Class<?> converterType, Class<?> targetType) {
        if (!isParameterizedTypeCompatible(converterType, targetType)) {
            throw new RuntimeException();
        }
    }

}
