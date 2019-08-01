package br.com.rooting.liquid.core.solid.tracker;

import br.com.rooting.liquid.core.solid.exception.NotFilledObjectException;

class StackCount {

    private static final Integer MIN_VALUE = 0;

    private Integer max;

    private Integer current;

    StackCount(Integer max) {
        this.max = max;
        this.current = MIN_VALUE;
    }

    void increment() {
        Integer newActual = this.current + 1;
        if (newActual > max) {
            throw new NotFilledObjectException();
        }
        this.current = newActual;
    }

    void decrement() {
        if (this.current > MIN_VALUE)
            this.current--;
    }

}
