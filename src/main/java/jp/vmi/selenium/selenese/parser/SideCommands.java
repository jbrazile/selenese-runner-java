package jp.vmi.selenium.selenese.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Side command information.
 */
public class SideCommands {

    /** Supported Selenium IDE version. */
    public static final String SUPPORTED_VERSION = "v3.3.1";

    @SuppressWarnings("javadoc")
    public static enum TargetType {
        // no target.
        NONE(null),
        // locator.
        LOCATOR("locator"),
        // region.
        REGION("region"),
        ;

        public final String name;

        TargetType(String name) {
            this.name = name;
        }
    }

    @SuppressWarnings("javadoc")
    public static class ArgType {
        public final String id;
        public final String name;
        public final String description;

        public ArgType(String id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }
    }

    @SuppressWarnings("javadoc")
    public class Command {
        public final String id;
        public final String name;
        public final String description;
        public final TargetType type;
        public final ArgType target;
        public final ArgType value;

        public Command(String id, String name, String description, String type, String target, String value) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.type = getTargetType(type);
            this.target = getArgType(target);
            this.value = getArgType(value);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(SideCommands.class);

    private static final String TARGET_TYPES_PREFIX = "TargetTypes.";
    private static final String ARG_TYPES_PREFIX = "ArgTypes.";

    private final Map<String, ArgType> argTypes = new HashMap<>();
    private final Map<String, Command> commandList = new LinkedHashMap<>();

    /**
     * Constructor.
     */
    public SideCommands() {
        try (BufferedReader r = new BufferedReader(
            new InputStreamReader(SideCommands.class.getResourceAsStream("/selenium-ide/Command.js"), StandardCharsets.UTF_8))) {
            parseCommandJs(r);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Pattern TARGET_TYPE_RE = Pattern.compile("\\s*(\\w+)\\s*:.*");

    private static String readTargetTypes(String firstLine, BufferedReader r) throws IOException {
        // export const TargetTypes = {
        if (!firstLine.matches("export\\s+const\\s+TargetTypes\\s*=\\s*\\{")) {
            log.trace("Skip: [{}]", firstLine);
            return null;
        }
        log.debug("Start checking TargetTypes.");
        String line;
        while ((line = r.readLine()) != null) {
            if (line.equals("};"))
                break;
            Matcher matcher = TARGET_TYPE_RE.matcher(line);
            if (!matcher.matches()) {
                log.warn("Unexpected line for reading TargetType: {}", line.trim());
                continue;
            }
            String type = matcher.group(1);
            try {
                TargetType.valueOf(type);
                log.debug("Type: {} - OK", type);
            } catch (IllegalArgumentException e) {
                log.warn("Unsupported target type: " + type);
            }
        }
        log.debug("End checking TargetTypes.");
        return "DONE";
    }

    private static void readPartial(BufferedReader r, String endMark, StringBuilder buf) throws IOException {
        String line;
        while ((line = r.readLine()) != null) {
            line = line.trim();
            if (line.equals(endMark))
                break;
            buf.append(line);
            int len = buf.length();
            if (len > 0 && buf.charAt(len - 1) == '\\')
                buf.deleteCharAt(len - 1);
        }
    }

    private static String readArgTypes(String firstLine, BufferedReader r) throws IOException {
        if (!firstLine.matches("export\\s+const\\s+ArgTypes\\s*=\\s*\\{")) {
            log.trace("Skip: [{}]", firstLine);
            return null;
        }
        StringBuilder buf = new StringBuilder("{");
        readPartial(r, "};", buf);
        buf.append('}');
        String result = buf.toString();
        log.trace("ArgTypes: {}", result);
        return result;
    }

    private static String readCommandList(String firstLine, BufferedReader r) throws IOException {
        if (!firstLine.matches("class\\s+CommandList\\s*\\{")) {
            log.trace("Skip: [{}]", firstLine);
            return null;
        }
        String line = r.readLine();
        if (line == null)
            throw new RuntimeException("\"Command.js\" is broken.");
        if (!line.matches("\\s*@observable\\s+list\\s+=\\s+new\\s+Map\\(\\[\\s*"))
            throw new RuntimeException("Unexpected input: [" + line + "]");
        StringBuilder buf = new StringBuilder("[");
        readPartial(r, "])", buf);
        buf.append(']');
        String result = buf.toString();
        log.trace("CommandList: {}", result);
        return result;
    }

    private void parseArgTypes(String argTypesStr) {
        @SuppressWarnings("unchecked")
        Map<String, Map<String, String>> map = new Gson().fromJson(argTypesStr, Map.class);
        map.forEach((key, value) -> {
            ArgType argType = new ArgType(key, value.get("name"), value.get("description"));
            this.argTypes.put(key, argType);
        });
    }

    private void parseCommandList(String commandListStr) {
        @SuppressWarnings("unchecked")
        List<List<Object>> list = new Gson().fromJson(commandListStr, List.class);
        list.forEach(item -> {
            String id = (String) item.get(0);
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) item.get(1);
            String name = map.get("name");
            String description = map.get("description");
            String type = map.get("type");
            String target = map.get("target");
            String value = map.get("value");
            Command command = new Command(id, name, description, type, target, value);
            this.commandList.put(id, command);
        });
    }

    private void parseCommandJs(BufferedReader r) throws IOException {
        String targetTypesStr = null;
        String argTypesStr = null;
        String commandListStr = null;
        String line;
        while ((line = r.readLine()) != null) {
            line = line.trim();
            if (targetTypesStr == null) {
                targetTypesStr = readTargetTypes(line, r);
            } else if (argTypesStr == null) {
                if ((argTypesStr = readArgTypes(line, r)) != null)
                    parseArgTypes(argTypesStr);
            } else if (commandListStr == null) {
                if ((commandListStr = readCommandList(line, r)) != null)
                    parseCommandList(commandListStr);
            }
        }
    }

    /**
     * Get target type.
     *
     * @param key target type.
     * @return TargetType.
     */
    public TargetType getTargetType(String key) {
        if (StringUtils.isEmpty(key))
            return TargetType.NONE;
        if (key.startsWith(TARGET_TYPES_PREFIX))
            key = key.substring(TARGET_TYPES_PREFIX.length());
        try {
            return TargetType.valueOf(key);
        } catch (IllegalArgumentException e) {
            return TargetType.NONE;
        }
    }

    /**
     * Get argument type.
     *
     * @param key argument type.
     * @return instance of ArgType.
     */
    public ArgType getArgType(String key) {
        if (StringUtils.isEmpty(key))
            return null;
        if (key.startsWith(ARG_TYPES_PREFIX))
            key = key.substring(ARG_TYPES_PREFIX.length());
        return argTypes.get(key);
    }

    /**
     * Get map of command list.
     *
     * @return map of command list.
     */
    public Map<String, Command> getCommandList() {
        return commandList;
    }
}
