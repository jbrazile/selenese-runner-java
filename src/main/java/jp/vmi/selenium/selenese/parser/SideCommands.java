package jp.vmi.selenium.selenese.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Side command information.
 */
public class SideCommands {

    /** Supported Selenium IDE version. */
    public static final String SUPPORTED_VERSION = "v3.3.1";

    private static final Logger log = LoggerFactory.getLogger(SideCommands.class);

    private static final Pattern TARGET_TYPE_RE = Pattern.compile("\\s*(\\w+)\\s*:.*");

    private static SideCommands instance = null;

    /**
     * Get instance of SideCommands.
     *
     * @return instance of SideCommands.
     */
    public static synchronized SideCommands getInstance() {
        if (instance == null)
            instance = new SideCommands();
        return instance;
    }

    private final Map<String, SideCommandInfo> commandInfoMap = new LinkedHashMap<>();

    private SideCommands() {
        try (BufferedReader r = new BufferedReader(
            new InputStreamReader(SideCommands.class.getResourceAsStream("/selenium-ide/Command.js"), StandardCharsets.UTF_8))) {
            parseCommandJs(r);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean verifyTargetTypes(String firstLine, BufferedReader r) throws IOException {
        // export const TargetTypes = {
        if (!firstLine.matches("export\\s+const\\s+TargetTypes\\s*=\\s*\\{")) {
            log.trace("Skip: [{}]", firstLine);
            return false;
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
                TargetTypes.valueOf(type);
                log.debug("Type: {} - OK", type);
            } catch (IllegalArgumentException e) {
                log.warn("Unsupported target type: " + type);
            }
        }
        log.debug("End checking TargetTypes.");
        return true;
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

    private boolean verifyArgTypes(String argTypesStr) {
        @SuppressWarnings("unchecked")
        Map<String, Map<String, String>> map = new Gson().fromJson(argTypesStr, Map.class);
        EnumSet<ArgTypes> all = EnumSet.allOf(ArgTypes.class);
        all.remove(ArgTypes.noArg);
        map.forEach((typeId, entry) -> {
            ArgTypes argType;
            try {
                argType = ArgTypes.valueOf(typeId);
            } catch (IllegalArgumentException e) {
                log.warn("ArgTypes.{} is not defined.", typeId);
                return;
            }
            String name = entry.get("name");
            String description = entry.get("description");
            String value = entry.get("value");
            String descLabel = "description";
            if (description == null) {// for ArgTypes.message
                description = value;
                descLabel = "value";
            }
            boolean matchName = argType.getName().equals(name);
            boolean matchDesc = argType.getDescription().equals(description);
            if (!matchName || !matchDesc) {
                log.warn("ArgTypes.{} is incompatible:", typeId, descLabel);
                if (!matchName)
                    log.warn("- name: [{}] != [{}]", argType.getName(), name);
                if (!matchDesc)
                    log.warn("- {}: [{}] != [{}]", descLabel, argType.getDescription(), description);
            }
            all.remove(argType);
        });
        if (!all.isEmpty())
            all.forEach(typeId -> log.warn("ArgTypes.{} is not defined in Command.js", typeId));
        return true;
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
            SideCommandInfo commandInfo = new SideCommandInfo(id, name, description,
                TargetTypes.parse(type), ArgTypes.parse(target), ArgTypes.parse(value));
            commandInfoMap.put(id, commandInfo);
        });
    }

    private void parseCommandJs(BufferedReader r) throws IOException {
        boolean isTargetTypesVerified = false;
        boolean isArgTypesVerified = false;
        String commandListStr = null;
        String line;
        while ((line = r.readLine()) != null) {
            line = line.trim();
            if (!isTargetTypesVerified) {
                isTargetTypesVerified = verifyTargetTypes(line, r);
            } else if (!isArgTypesVerified) {
                String argTypesStr = readArgTypes(line, r);
                if (argTypesStr != null)
                    isArgTypesVerified = verifyArgTypes(argTypesStr);
            } else if (commandListStr == null) {
                if ((commandListStr = readCommandList(line, r)) != null)
                    parseCommandList(commandListStr);
            }
        }
    }

    /**
     * Get map of command list.
     *
     * @return map of command list.
     */
    public Map<String, SideCommandInfo> getCommandInfoMap() {
        return commandInfoMap;
    }
}
