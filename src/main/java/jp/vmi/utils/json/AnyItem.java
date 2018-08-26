package jp.vmi.utils.json;

class AnyItem extends AbstractItem {

    private Object value = null;

    public AnyItem() {
        super(ItemType.ANY);
    }

    @Override
    public <T> void setValue(T value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue() {
        return (T) value;
    }
}
