package jp.vmi.selenium.selenese.parser;

/**
 * Side command information.
 */
@SuppressWarnings("javadoc")
public class SideCommandInfo {
    public final String id;
    public final String name;
    public final String description;
    public final TargetTypes type;
    public final ArgTypes target;
    public final ArgTypes value;

    public SideCommandInfo(String id, String name, String description, TargetTypes type, ArgTypes target, ArgTypes value) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.target = target;
        this.value = value;
    }

    public boolean isTargetScript() {
        return target == ArgTypes.script || target == ArgTypes.conditionalExpression;
    }

    public boolean isValueScript() {
        return value == ArgTypes.script;
    }
}
