package com.hyd.gtpb;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * 可变的任意对象，用于在 Lambda 表达式中赋值
 *
 * @author yiding_he
 */
public class Value<T> {

    public static <T> Value<T> of(T t) {
        return new Value<>(t);
    }

    public static <T> Value<T> empty() {
        return new Value<>(null);
    }

    private T t;

    public Value(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

    public Optional<T> optional() {
        return Optional.ofNullable(t);
    }

    public void set(T t) {
        this.t = t;
    }

    public boolean isEmpty() {
        return t == null;
    }

    public void ifNotEmpty(Consumer<T> consumer) {
        if (!isEmpty()) {
            consumer.accept(t);
        }
    }
}
