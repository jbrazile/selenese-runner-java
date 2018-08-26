package jp.vmi.utils.json;

import java.lang.reflect.ParameterizedType;
import java.util.List;

@SuppressWarnings("rawtypes")
class ListItem extends AbstractItem<List> {

    private final Class<?> elemType;

    ListItem(ParameterizedType pType) {
        super(List.class);
        this.elemType = (Class<?>) pType.getActualTypeArguments()[0];
    }

    @Override
    public List getValue() {
        return null;
    }
}