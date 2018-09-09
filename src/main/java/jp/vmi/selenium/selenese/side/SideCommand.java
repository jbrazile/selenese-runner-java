package jp.vmi.selenium.selenese.side;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * "command" element of side format.
 */
@SuppressWarnings("javadoc")
public class SideCommand {

    private String id;
    private String command;
    private String target;
    private List<String> targets;
    private String value;
    private String comment;

    public SideCommand() {
        this(false);
    }

    public SideCommand(boolean isGen) {
        if (isGen) {
            id = UUID.randomUUID().toString();
            targets = new ArrayList<>();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<String> getTargets() {
        return targets;
    }

    public void setTargets(List<String> targets) {
        this.targets = targets;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
