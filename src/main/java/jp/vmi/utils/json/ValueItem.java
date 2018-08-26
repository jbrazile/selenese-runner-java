package jp.vmi.utils.json;

class ValueItem extends AbstractItem {

    private final Class<?> clazz;
    private Object value = null;

    ValueItem(Class<?> clazz) {
        super(ItemType.VALUE);
        this.clazz = clazz;
    }

    @Override
    public <T> void setValue(T value) {
        if (clazz.isAssignableFrom(value.getClass()))
            this.value = value;
        else
            throw new IllegalArgumentException(String.format("Can't set (%s) %s to %s", value.getClass(), value, clazz));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue() {
        return (T) value;
    }
}
