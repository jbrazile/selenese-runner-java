package jp.vmi.utils.json;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

enum ItemType {
    // any type
    ANY(),

    // scalar type
    VALUE(),

    // map
    MAP(Map.class),

    // list
    LIST(List.class),

    // structed object
    OBJECT(),
    ;

    public final Type type;

    private ItemType() {
        this.type = new Type() {
        };
    }

    private ItemType(Type type) {
        this.type = type;
    }
}
