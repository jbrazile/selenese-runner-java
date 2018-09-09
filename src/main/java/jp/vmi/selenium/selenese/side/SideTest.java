package jp.vmi.selenium.selenese.side;

import java.util.ArrayList;
import java.util.List;

/**
 * "test" element of side format.
 */
@SuppressWarnings("javadoc")
public class SideTest extends SideBase {

    private List<SideCommand> commands;

    public SideTest() {
        this(false);
    }

    public SideTest(boolean isGen) {
        super(isGen);
        if (isGen)
            commands = new ArrayList<>();
    }

    public List<SideCommand> getCommands() {
        return commands;
    }

    public void setCommands(List<SideCommand> commands) {
        this.commands = commands;
    }

    public void addCommand(SideCommand sideCommand) {
        commands.add(sideCommand);
    }
}
