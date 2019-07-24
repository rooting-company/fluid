package br.com.rooting.liquid.core;

public class StackCount {

    private Integer max;

    private Integer actual;

    StackCount(Integer max, Integer actual) {
        this.max = max;
        this.actual = actual;
    }

    void increment() {
        this.actual++;

        if (this.actual > max) {
            throw new NotFilledObjectException();
        }
    }

    void decrement() {
        this.actual--;
    }

    public boolean isEmpty() {
        return this.actual <= 0;
    }

}
