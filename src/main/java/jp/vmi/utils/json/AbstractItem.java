package jp.vmi.utils.json;

abstract class AbstractItem<T> implements Item<T> {

    final Class<T> rawType;

    AbstractItem(Class<T> rawType) {
        this.rawType = rawType;
    }

    @Override
    public Class<T> getRawType() {
        return rawType;
    }
}
