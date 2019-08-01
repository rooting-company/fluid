package br.com.rooting.liquid.core.solid.tracker;

import java.util.HashMap;
import java.util.Map;

public class Tracker {

    private Map<Class<?>, StackCount> stack = new HashMap<>();

    public void track(Object object) {
        Class<?> type = object.getClass();
        StackCount count = stack.get(type);

        if (count != null) {
            count.increment();
            return;
        }
        stack.put(type, new StackCount(1));
    }

    public void untrack(Object object) {
        Class<?> type = object.getClass();
        StackCount count = stack.get(type);
        count.decrement();
    }

}
