package jp.vmi.selenium.selenese.parser;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("javadoc")
public enum TargetTypes {
    // no target.
    NONE(null),
    // locator.
    LOCATOR("locator"),
    // region.
    REGION("region"),
    ;

    private static final String PREFIX = "TargetTypes.";

    public final String name;

    TargetTypes(String name) {
        this.name = name;
    }

    /**
     * Parse target type string.
     *
     * @param typeStr target type string.
     * @return TargetTypes.
     */
    public static TargetTypes parse(String typeStr) {
        if (StringUtils.isEmpty(typeStr))
            return TargetTypes.NONE;
        if (typeStr.startsWith(PREFIX))
            typeStr = typeStr.substring(PREFIX.length());
        try {
            return TargetTypes.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            return TargetTypes.NONE;
        }
    }
}
