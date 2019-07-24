package br.com.rooting.liquid.core;

public class Node {

    private final Class<?> type;

    private final Integer max;

    private final Integer actual;

    public Node(Class<?> type, Integer max, Integer actual) {
        this.type = type;
        this.max = max;
        this.actual = actual;
    }

    public boolean isParent(Class<?> type) {
        return this.type.equals(type);
    }

    public Integer getMax() {
        return max;
    }

    public Integer getActual() {
        return actual;
    }
}
