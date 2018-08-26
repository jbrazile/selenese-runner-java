package jp.vmi.utils.json;

import java.util.HashMap;
import java.util.Map;

class MapItem extends AbstractItem {

    private final Map<?, ?> value = new HashMap<>();

    MapItem() {
        super(ItemType.MAP);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue() {
        return (T) value;
    }
}
