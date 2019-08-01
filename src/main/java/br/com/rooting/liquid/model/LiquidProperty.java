package br.com.rooting.liquid.model;

public class LiquidProperty {

    private final String key;

    private final String value;

    public LiquidProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
       return this.key + ": " + this.value;
    }
}
