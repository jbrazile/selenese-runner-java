package jp.vmi.utils.json;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static jp.vmi.utils.json.ItemType.*;

final class Items {

    private Items() {
        // no operation.
    }

    public static Item newItem(Type type) {
        if (type == ANY.type)
            return new AnyItem();
        if (!(type instanceof Class))
            throw new UnsupportedOperationException("Unsupported type: " + type);
        Class<?> clazz = (Class<?>) type;
        if (Map.class.isAssignableFrom(clazz))
            return new MapItem();
        else if (List.class.isAssignableFrom(clazz))
            return new ListItem();
        else if (clazz.isPrimitive() || CharSequence.class.isAssignableFrom(clazz) || Number.class.isAssignableFrom(clazz))
            return new ValueItem(clazz);
        else
            return new ObjectItem(clazz);
    }
}
