package br.com.rooting.liquid.core;

import java.util.HashMap;
import java.util.Map;

class Tracker {

//    private Deque<Node> nodes = new ArrayDeque<>();
//
//    public void track(Object object) {
//        Class<?> type = object.getClass();
//        if (!nodes.isEmpty()) {
//            Node previous = nodes.peek();
//            if (previous.isParent(type)) {
//                Integer actual = previous.getActual() + 1;
//
//                if (actual > previous.getMax()) {
//                    throw new NotFilledObjectException();
//                }
//
//                nodes.push(new Node(type, previous.getMax(), actual));
//                return;
//            }
//        }
//        nodes.push(new Node(type, 1, 0));
//    }
//
//    public void untrack() {
//        nodes.poll();
//    }

    private Map<Class<?>, StackCount> stack = new HashMap<>();

    void track(Object object) {
        Class<?> type = object.getClass();
        StackCount count = stack.get(type);

        if (count != null) {
            count.increment();
            return;
        }
        stack.put(type, new StackCount(1, 0));
    }

    void untrack(Object object) {
        Class<?> type = object.getClass();
        StackCount count = stack.get(type);
        count.decrement();
    }

}
