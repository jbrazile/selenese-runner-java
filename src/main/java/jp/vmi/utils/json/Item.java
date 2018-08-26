package jp.vmi.utils.json;

interface Item<T> {

    default void setValue(T value) {
        throw new UnsupportedOperationException();
    }

    T getValue();

    Class<T> getRawType();
}
