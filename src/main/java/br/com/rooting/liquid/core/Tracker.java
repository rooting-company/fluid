package br.com.rooting.liquid.core;

import java.util.HashMap;
import java.util.Map;

class Tracker {

    private Map<Class<?>, StackCount> stack = new HashMap<>();

    void track(Object object) {
        Class<?> type = object.getClass();
        StackCount count = stack.get(type);

        if (count != null) {
            count.increment();
            return;
        }
        stack.put(type, new StackCount(1));
    }

    void untrack(Object object) {
        Class<?> type = object.getClass();
        StackCount count = stack.get(type);
        count.decrement();
    }

}
