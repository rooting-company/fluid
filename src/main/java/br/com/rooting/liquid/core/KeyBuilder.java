package br.com.rooting.liquid.core;

import br.com.rooting.liquid.mapping.Alias;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Deque;

class KeyBuilder {

    private String key;

    private Deque<Integer> orderDeque;

    KeyBuilder(Object object) {
        this.key = getPrefix(object);
        this.orderDeque = new ArrayDeque<>();
    }

    private String getPrefix(Object object) {
        Alias alias = object.getClass().getAnnotation(Alias.class);
        if (alias != null)
            return alias.value();

        return StringUtils.uncapitalize(object.getClass().getSimpleName());
    }

    KeyBuilder next(Field field) {
        this.key = this.updateKey(this.key, field);
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
        this.key = this.removeLastPart(this.key);
        return this;
    }

    private String removeLastPart(String key) {
        int lastDotIndex = key.lastIndexOf('.');

        if (lastDotIndex> 0)
            return key.substring(0, lastDotIndex);

        return key;
    }

    String build() {
        return this.key;
    }

    KeyBuilder incrementOrder() {
        Integer lastOrder = this.orderDeque.poll();
        Integer newOrder = lastOrder + 1;
        this.orderDeque.push(newOrder);
        this.key = this.key.replaceFirst("\\[\\d\\]$", "[" + newOrder + "]");
        return this;
    }

    KeyBuilder nextOrder() {
        this.orderDeque.push(0);
        this.key = this.key.concat("[" + 0 + "]");
        return this;
    }

    KeyBuilder previousOrder() {
        this.orderDeque.poll();
        this.key = this.key.replaceFirst("\\[\\d\\]$", "");
        return this;
    }

}
