package jp.vmi.utils.beans;

import java.lang.reflect.Type;
import java.util.HashMap;

final class AccessorMap<T> extends HashMap<String, Accessor<T>> {

    private static final long serialVersionUID = 1L;

    public Getter<T> getGetter(String propName) {
        Accessor<T> accessor = get(propName);
        return accessor != null ? accessor.getter : null;
    }

    public Setter<T> getSetter(String propName) {
        Accessor<T> accessor = get(propName);
        return accessor != null ? accessor.setter : null;
    }

    public void putAccessor(String propName, Type type, Getter<T> getter, Setter<T> setter) {
        put(propName, new Accessor<T>(propName, type, getter, setter));
    }

    private Accessor<T> getAccessor(String propName) {
        Accessor<T> accessor = get(propName);
        if (accessor == null)
            throw new IllegalArgumentException("Missing property: " + propName);
        return accessor;
    }

    public Object getValue(String propName, T object) {
        return getAccessor(propName).getter.get(object);
    }

    public void setValue(String propName, T object, Object value) {
        getAccessor(propName).setter.set(object, value);
    }

    public Type getValueType(String propName) {
        return getAccessor(propName).type;
    }
}
