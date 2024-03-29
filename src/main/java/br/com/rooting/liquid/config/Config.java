package br.com.rooting.liquid.config;

import br.com.rooting.liquid.converter.ConverterRepository;

public final class Config {

    private final boolean enableCyclicReference;

    private final boolean replaceNotNullValues;

    private final boolean liquifyNullvalues;

    private final boolean enableCache;

    private final ConverterRepository converterRepository;

    Config(boolean enableCyclicReference,
           boolean replaceNotNullValues,
           boolean liquifyNullvalues,
           boolean enableCache,
           ConverterRepository converterRepository) {

        this.enableCyclicReference = enableCyclicReference;
        this.replaceNotNullValues = replaceNotNullValues;
        this.liquifyNullvalues = liquifyNullvalues;
        this.enableCache = enableCache;
        this.converterRepository = converterRepository;
    }

    public boolean isEnableCyclicReference() {
        return enableCyclicReference;
    }

    public boolean isReplaceNotNullValues() {
        return replaceNotNullValues;
    }

    public boolean isLiquifyNullvalues() {
        return liquifyNullvalues;
    }

    public boolean isEnableCache() {
        return enableCache;
    }

    public ConverterRepository getConverterRepository() {
        return converterRepository;
    }

}
