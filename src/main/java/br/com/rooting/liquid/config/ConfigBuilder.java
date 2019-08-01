package br.com.rooting.liquid.config;

import br.com.rooting.liquid.converter.ConverterRepository;
import br.com.rooting.liquid.converter.LiquifyConverter;
import br.com.rooting.liquid.converter.SolidifyConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConfigBuilder {

    private boolean enableCyclicReference = false;

    private boolean replaceNotNullValues = false;

    private boolean liquifyNullValues = false;

    private boolean enableCache = true;

    private List<LiquifyConverter<?>> liquifyConverters = new ArrayList<>();

    private List<SolidifyConverter<?>> solidifyConverters = new ArrayList<>();

    public ConfigBuilder() {}

    public ConfigBuilder withEnableCyclicReference(boolean value) {
        this.enableCyclicReference = value;
        return this;
    }

    public ConfigBuilder withReplaceNotNullValues(boolean value) {
        this.replaceNotNullValues = value;
        return this;
    }

    public ConfigBuilder withLiquifyNullValues(boolean value) {
        this.liquifyNullValues = value;
        return this;
    }

    public ConfigBuilder withEnableCache(boolean value) {
        this.enableCache = value;
        return this;
    }

    public ConfigBuilder addLiquifyConveter(LiquifyConverter<?> converter) {
        this.liquifyConverters.add(converter);
        return this;
    }

    public ConfigBuilder addLiquifyConverters(Collection<LiquifyConverter<?>> converters) {
        this.liquifyConverters.addAll(converters);
        return this;
    }

    public ConfigBuilder addSolidifyConveter(SolidifyConverter<?> converter) {
        this.solidifyConverters.add(converter);
        return this;
    }
    public ConfigBuilder addSolidifyConverters(Collection<SolidifyConverter<?>> converters) {
        this.solidifyConverters.addAll(converters);
        return this;
    }

    public void clean() {
        this.enableCyclicReference = false;
        this.replaceNotNullValues = false;
        this.enableCache = true;
        this.liquifyConverters = new ArrayList<>();
        this.solidifyConverters = new ArrayList<>();
    }

    public Config build() {
        ConverterRepository converterRepository = new ConverterRepository(liquifyConverters, solidifyConverters);
        return new Config(enableCyclicReference,
                          replaceNotNullValues,
                          liquifyNullValues,
                          enableCache,
                          converterRepository);
    }

}
