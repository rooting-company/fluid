package br.com.rooting.liquid.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;

public class LiquidObject {

    private final Map<String, LiquidProperty> propertiesMap;

    private LiquidObject(Map<String, LiquidProperty> propertiesMap) {
        this.propertiesMap = propertiesMap;
    }

    public static LiquidObject create(List<LiquidProperty> properties) {
        Map<String, LiquidProperty> map = properties.stream().collect(toMap(LiquidProperty::getKey, p -> p));
        return new LiquidObject(unmodifiableMap(map));
    }

    public LiquidProperty findProperty(String key) {
        return propertiesMap.get(key);
    }

    public List<LiquidProperty> asList() {
        return new ArrayList<>(propertiesMap.values());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        propertiesMap.forEach((k, v) -> builder.append(v.toString()).append(";"));
        return builder.toString();
    }
}
