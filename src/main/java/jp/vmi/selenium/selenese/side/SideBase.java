package jp.vmi.selenium.selenese.side;

import java.util.UUID;

/**
 * base element of side format.
 */
@SuppressWarnings("javadoc")
public abstract class SideBase {

    private String id;
    private String name;

    public SideBase(boolean isGen) {
        if (isGen)
            id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return Side.toJson(this);
    }
}
