package jp.vmi.utils.json;

import java.lang.reflect.InvocationTargetException;

import jp.vmi.utils.beans.BeanMap;

class ObjectItem extends AbstractItem {

    private final BeanMap<Object, Object> value;

    ObjectItem(Class<?> clazz) {
        super(ItemType.OBJECT);
        try {
            Object object = clazz.getConstructor().newInstance();
            this.value = new BeanMap<Object, Object>(object);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue() {
        return (T) value;
    }
}
