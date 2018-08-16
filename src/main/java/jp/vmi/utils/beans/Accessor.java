package jp.vmi.utils.beans;

import java.lang.reflect.Type;

final class Accessor<T> {

    private static final String NO_SETTER_MSG_FMT = "Can't set a value for property \"%s\"";

    public final String name;
    public final Type type;
    public final Getter<T> getter;
    public final Setter<T> setter;

    Accessor(String name, Type type, Getter<T> getter, Setter<T> setter) {
        this.name = name;
        this.type = type;
        this.getter = getter;
        this.setter = setter != null ? setter : this::noSetter;
    }

    public void noSetter(T object, Object value) {
        throw new UnsupportedOperationException(String.format(NO_SETTER_MSG_FMT, name));
    }
}
