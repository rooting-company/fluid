package br.com.rooting.liquid.core;

import br.com.rooting.liquid.mapping.Alias;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Deque;

class KeyBuilder {

    private Deque<String> keyDeque;

    private Deque<Integer> orderDeque;

    KeyBuilder(Object object) {
        this.keyDeque = new ArrayDeque<>();
        this.orderDeque = new ArrayDeque<>();
        this.keyDeque.push(getPrefix(object));
    }

    private String getPrefix(Object object) {
        Alias alias = object.getClass().getAnnotation(Alias.class);
        if (alias != null)
            return alias.value();

        return StringUtils.uncapitalize(object.getClass().getSimpleName());
    }

    KeyBuilder next(Field field) {
        this.keyDeque.push(this.updateKey(this.keyDeque.peek(), field));
        return this;
    }

    private String updateKey(String key, Field field) {
        if (!key.endsWith("."))
            key = key.concat(".");

        Alias alias = field.getAnnotation(Alias.class);
        if (alias != null) {
            if(alias.root())
                return alias.value();

            return key.concat(alias.value());
        }

        return key.concat(field.getName());
    }

    KeyBuilder previous() {
        this.keyDeque.poll();
        return this;
    }

    String build() {
        return this.keyDeque.peek();
    }

    KeyBuilder incrementOrder() {
        Integer lastOrder = this.orderDeque.poll();
        Integer newOrder = lastOrder + 1;
        this.orderDeque.push(newOrder);

        String key = this.keyDeque.poll();
        key = key.replaceFirst("\\[\\d\\]$", "[" + newOrder + "]");
        this.keyDeque.push(key);
        return this;
    }

    KeyBuilder nextOrder() {
        this.orderDeque.push(0);
        String key = this.keyDeque.poll();
        key = key.concat("[" + 0 + "]");
        this.keyDeque.push(key);
        return this;
    }

    KeyBuilder previousOrder() {
        this.orderDeque.poll();
        return this;
    }

}
