package jp.vmi.utils.beans;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Bean map.
 *
 * @param <T> Type of bean.
 * @param <V> Type of value of bean map.
 */
public class BeanMap<T, V> extends AbstractMap<String, V> {

    // cache of AccessorMap.
    private static final WeakHashMap<Class<?>, AccessorMap<?>> accessorMaps = new WeakHashMap<>();

    private static String getPropNameFromMethodName(String methodName, int offset) {
        int nextOffset = methodName.offsetByCodePoints(offset, 1);
        return methodName.substring(offset, nextOffset).toLowerCase(Locale.ROOT) + methodName.substring(nextOffset);
    }

    private static String getPropNameFromGetterMethod(Method m) {
        if (m.getParameterCount() != 0 || m.getReturnType() == void.class)
            return null;
        String name = m.getName();
        int len = name.length();
        if (name.startsWith("get"))
            return len > 3 ? getPropNameFromMethodName(name, 3) : null;
        else if (name.startsWith("is"))
            return len > 2 ? getPropNameFromMethodName(name, 2) : null;
        else
            return null;
    }

    private static String getPropNameFromSetterMethod(Method m) {
        if (m.getParameterCount() != 1)
            return null;
        String name = m.getName();
        int len = name.length();
        if (name.startsWith("set") && len > 3)
            return getPropNameFromMethodName(name, 3);
        else
            return null;
    }

    private static <T> Getter<T> fieldToGetterLambda(Lookup lookup, Field f) throws IllegalAccessException {
        MethodHandle mh = lookup.unreflectGetter(f);
        @SuppressWarnings("unchecked")
        Getter<T> getter = MethodHandleProxies.asInterfaceInstance(Getter.class, mh);
        return getter;
    }

    private static <T> Setter<T> fieldToSetterLambda(Lookup lookup, Field f) throws IllegalAccessException {
        MethodHandle mh = lookup.unreflectSetter(f);
        @SuppressWarnings("unchecked")
        Setter<T> setter = MethodHandleProxies.asInterfaceInstance(Setter.class, mh);
        return setter;
    }

    private static <T> Getter<T> getterMethodToLambda(Lookup lookup, Method m) throws IllegalAccessException {
        MethodHandle mh = lookup.unreflect(m);
        MethodType mt = mh.type();
        try {
            CallSite callSite = LambdaMetafactory.metafactory(lookup, "get", MethodType.methodType(Getter.class), mt.generic(), mh, mt);
            return (Getter<T>) callSite.getTarget().invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> Setter<T> setterMethodToLambda(Lookup lookup, Method m) throws IllegalAccessException {
        MethodHandle mh = lookup.unreflect(m);
        MethodType mt = mh.type();
        MethodType gmt = mt.generic();
        try {
            if (mt.returnType() == Void.TYPE) {
                gmt = gmt.changeReturnType(Void.TYPE);
                CallSite cs = LambdaMetafactory.metafactory(lookup, "set", MethodType.methodType(Setter.class), gmt, mh, mt);
                return (Setter<T>) cs.getTarget().invoke();
            } else {
                CallSite cs = LambdaMetafactory.metafactory(lookup, "set", MethodType.methodType(Setter2.class), gmt, mh, mt);
                Setter2<T> setter2 = (Setter2<T>) cs.getTarget().invoke();
                return (object, value) -> {
                    setter2.set(object, value);
                };
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> AccessorMap<T> registerAccessors(Lookup lookup, AccessorMap<T> accessorMap, Map<String, TransientType> transientMap, Class<? super T> clazz) {
        Class<? super T> superclass = clazz.getSuperclass();
        if (superclass != null && superclass != Object.class)
            registerAccessors(lookup, accessorMap, transientMap, superclass);
        try {
            // scan all fields for chceking transient.
            for (Field f : clazz.getDeclaredFields()) {
                int mod = f.getModifiers();
                if (Modifier.isStatic(mod))
                    continue;
                String propName = f.getName();
                TransientType tType = TransientType.geTransientType(f, mod);
                transientMap.put(propName, tType);
                if (tType == TransientType.TRUE || !Modifier.isPublic(mod))
                    continue;
                Getter<T> getter = fieldToGetterLambda(lookup, f);
                Setter<T> setter = !Modifier.isFinal(mod) ? fieldToSetterLambda(lookup, f) : null;
                Type type = f.getGenericType();
                accessorMap.putAccessor(propName, type, getter, setter);
            }
            // public method only.
            for (Method m : clazz.getMethods()) {
                int mod = m.getModifiers();
                if (Modifier.isStatic(mod) || "getClass".equals(m.getName()))
                    continue;
                String propName;
                boolean isGetter;
                if ((propName = getPropNameFromGetterMethod(m)) != null)
                    isGetter = true;
                else if ((propName = getPropNameFromSetterMethod(m)) != null)
                    isGetter = false;
                else
                    continue;
                TransientType tType = transientMap.getOrDefault(propName, TransientType.NONE).updatedBy(m, mod);
                switch (tType) {
                case TRUE:
                    transientMap.put(propName, tType);
                    continue;
                case FALSE:
                    transientMap.put(propName, tType);
                    break;
                case NONE:
                    break;
                }
                Getter<T> getter;
                Setter<T> setter;
                Type type;
                if (isGetter) {
                    getter = getterMethodToLambda(lookup, m);
                    setter = accessorMap.getSetter(propName);
                    type = m.getGenericReturnType();
                } else {
                    Accessor<T> accessor = accessorMap.get(propName);
                    if (accessor != null) {
                        getter = accessor.getter;
                        type = accessor.type;
                    } else {
                        getter = null;
                        type = null;
                    }
                    setter = setterMethodToLambda(lookup, m);
                }
                accessorMap.putAccessor(propName, type, getter, setter);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        accessorMap.entrySet().removeIf(entry -> entry.getValue().getter == null);
        return accessorMap;
    }

    private final T object;
    private final AccessorMap<T> accessorMap;

    /**
     * Constructor.
     *
     * @param object bean object.
     */
    @SuppressWarnings("unchecked")
    public BeanMap(T object) {
        this.object = object;
        Class<T> clazz = (Class<T>) object.getClass();
        AccessorMap<T> accessorMap;
        synchronized (accessorMaps) {
            accessorMap = (AccessorMap<T>) accessorMaps.get(clazz);
        }
        if (accessorMap == null) {
            accessorMap = registerAccessors(MethodHandles.lookup(), new AccessorMap<>(), new HashMap<>(), clazz);
            synchronized (accessorMaps) {
                accessorMaps.put(clazz, accessorMap);
            }
        }
        this.accessorMap = accessorMap;
    }

    @Override
    public int size() {
        return accessorMap.size();
    }

    @Override
    public boolean isEmpty() {
        return accessorMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return accessorMap.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(Object key) {
        return (V) accessorMap.getValue(key.toString(), object);
    }

    @Override
    public V put(String key, V value) {
        V prevValue;
        try {
            prevValue = get(key);
        } catch (UnsupportedOperationException e) {
            prevValue = null;
        }
        accessorMap.setValue(key, object, value);
        return prevValue;
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
        return accessorMap.keySet();
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return new AbstractSet<Entry<String, V>>() {
            @Override
            public Iterator<Entry<String, V>> iterator() {
                return new Iterator<Entry<String, V>>() {

                    private final Iterator<String> keyIter = BeanMap.this.keySet().iterator();

                    @Override
                    public boolean hasNext() {
                        return keyIter.hasNext();
                    }

                    @Override
                    public Entry<String, V> next() {
                        String key = keyIter.next();
                        V value = BeanMap.this.get(key);
                        return new SimpleEntry<>(key, value);
                    }
                };
            }

            @Override
            public int size() {
                return BeanMap.this.size();
            }
        };
    }

    /**
     * Get type of value.
     *
     * @param key key.
     * @return type of value.
     */
    public Type getValueType(String key) {
        return accessorMap.getValueType(key);
    }
}
