package jp.vmi.utils.json;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.openqa.selenium.json.Json;
import org.openqa.selenium.json.JsonException;
import org.openqa.selenium.json.JsonInput;
import org.openqa.selenium.json.PropertySetting;
import org.openqa.selenium.json.TypeCoercer;

import jp.vmi.utils.beans.BeanMap;

/**
 * JSON parser.
 */
public final class JsonParser extends TypeCoercer<Object> {

    @Override
    public boolean test(Class<?> clazz) {
        return true;
    }

    /**
     * Parse JSON and return object of type.
     *
     * @param r Reader object.
     * @param clazz class for parsing.
     * @return parsed object.
     */
    public static <T> T parse(Reader r, Class<T> clazz) {
        return new Json().newInput(r).addCoercers(new JsonParser()).read(clazz);
    }

    @Override
    public BiFunction<JsonInput, PropertySetting, Object> apply(Type type) {
        return (jsonInput, propertySetting) -> {
            return parseType(jsonInput, type);
        };
    }

    private <T> T parseType(JsonInput jsonInput, Type type) {
        Item item = Items.newItem(type);
        switch (jsonInput.peek()) {
        case NAME:
        case END_MAP:
        case END_COLLECTION:
        case END:
            throw new JsonException("Invalid format");
        case BOOLEAN:
            item.setValue(jsonInput.nextBoolean());
            break;
        case NUMBER:
            item.setValue(jsonInput.nextNumber());
            break;
        case STRING:
            item.setValue(jsonInput.nextString());
            break;
        case NULL:
            item.setValue(jsonInput.nextNull());
            break;
        case START_MAP:
            Map<String, Object> map = item.getValue();
            if (map == null) {
                map = new HashMap<>();
                item.setValue(map);
            }
            parseMap(jsonInput, map);
            break;
        case START_COLLECTION:
            List<Object> list = item.getValue();
            if (list == null) {
                list = new ArrayList<>();
                item.setValue(list);
            }
            parseList(jsonInput, list);
            break;
        }
        return item.getValue();
    }

    private Map<String, Object> parseMap(JsonInput jsonInput, Item item) {
        jsonInput.beginObject();
        Map<String, Object> map = item.getValue();
        if (map == null) {
            item.getType();

        }
        loop: while (true) {
            String name;
            switch (jsonInput.peek()) {
            case NAME:
                name = jsonInput.nextName();
                break;
            case END_MAP:
                break loop;
            default:
                throw new JsonException("Invalid format");
            }
            switch (jsonInput.peek()) {
            case NAME:
            case END_MAP:
            case END_COLLECTION:
            case END:
                throw new JsonException("Invalid format");
            case NULL:
                map.put(name, jsonInput.nextNull());
                break;
            case BOOLEAN:
                map.put(name, jsonInput.nextBoolean());
                break;
            case NUMBER:
                map.put(name, jsonInput.nextNumber());
                break;
            case STRING:
                map.put(name, jsonInput.nextString());
                break;
            case START_MAP:
                Map<?, ?> childmap = parseType(jsonInput, Map.class);
                map.put(name, childmap);
                break;
            case START_COLLECTION:
                if (map instanceof BeanMap) {
                    Type valueType = ((BeanMap) map).getValueType(name);

                }
                List<?> childlist = parseType(jsonInput, List.class);
                map.put(name, childlist);
                break;
            }
        }
        jsonInput.endObject();
        return map;
    }

    private List<Object> parseList(JsonInput jsonInput, List<Object> list) {
        jsonInput.beginArray();
        loop: while (true) {
            switch (jsonInput.peek()) {
            case END_COLLECTION:
                break loop;
            case NAME:
            case END_MAP:
            case END:
                throw new JsonException("Invalid format");
            case NULL:
                list.add(jsonInput.nextNull());
                break;
            case BOOLEAN:
                list.add(jsonInput.nextBoolean());
                break;
            case NUMBER:
                list.add(jsonInput.nextNumber());
                break;
            case STRING:
                list.add(jsonInput.nextString());
                break;
            case START_MAP:
                Map<?, ?> childmap = parseType(jsonInput, Map.class);
                list.add(childmap);
                break;
            case START_COLLECTION:
                List<?> childlist = parseType(jsonInput, List.class);
                list.add(childlist);
                break;
            }
        }
        return list;
    }
}
