package jp.vmi.selenium.selenese.parser;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Argument types of side command.
 *
 * Compatible with Command.js on Selenium IDE TNG.
 */
@SuppressWarnings("javadoc")
public enum ArgTypes {
    // Argument type of Selenese Runner Java only.

    // no argument. use this instead of null.
    noArg("-", "-"),

    cssLocator("css locator",
        "An element locator, implies \"css=\""),

    // Argument types of Selenium IDE TNG.

    answer("answer",
        "The answer to give in response to the prompt pop-up."),

    alertText("alert text",
        "text to check"),

    attributeLocator("attribute locator",
        "An element locator followed by an @ sign and then the name of the attribute, e.g. \"foo@bar\"."),

    conditionalExpression("conditional expression",
        "JavaScript expression that returns a boolean result for use in control flow commands."),

    coord("coord String",
        "Specifies the x,y position (e.g., - 10,20) of the mouse event relative to the element found from a locator."),

    expectedValue("expected value",
        "The result you expect a variable to contain (e.g., true, false,or some other value)."),

    expression("expression",
        "The value you'd like to store."),

    formLocator("form locator",
        "An element locator for the form you want to submit."),

    keySequence("key sequence",
        "A sequence of keys to type, can be used to send key strokes (e.g.${KEY_ENTER})."),

    locator("locator",
        "An element locator."),

    locatorOfObjectToBeDragged("locator of object to be dragged",
        "The locator of element to be dragged."),

    locatorOfDragDestinationObject("locator of drag destination object",
        "The locator of an element whose location (e.g., the center-most pixel within it) will be the point where locator of object to be dragged is dropped."),

    optionLocator("option",
        "An option locator, typically just an option label (e.g. \"John Smith\")."),

    message("message",
        "The message to print."),

    pattern("text",
        "An exact string match. Support for pattern matching is in the works. See https://github.com/SeleniumHQ/selenium-ide/issues/141 for details."),

    region("region",
        "Specify a rectangle with coordinates and lengths (e.g., \"x: 257, y: 300, width: 462, height: 280\")."),

    resolution("resolution",
        "Specify a window resolution using WidthxHeight. (e.g., 1280x800)."),

    script("script",
        "The JavaScript snippet to run."),

    selectLocator("select locator",
        "An element locator identifying a drop-down menu."),

    testCase("test case",
        "Test case name from the project."),

    text("text",
        "The text to verify."),

    times("times",
        "The number of attempts a times control flow loop will execute the commands within its block."),

    url("url",
        "The URL to open (may be relative or absolute)."),

    value("value",
        "The value to type."),

    variableName("variable name",
        "The name of a variable (without brackets). Used to either store an expression's result in or reference for a check (e.g., with 'assert' or 'verify')."),

    waitTime("wait time",
        "The amount of time to wait (in milliseconds)."),

    window("window",
        "The id of the browser window to select."),

    xpath("xpath",
        "The xpath expression to evaluate."),

    ;

    private static final Logger log = LoggerFactory.getLogger(ArgTypes.class);

    private static final String PREFIX = "ArgTypes.";

    private final String name;
    private final String description;

    ArgTypes(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Parse argument type string.
     *
     * @param typeStr argument type string.
     * @return instance of ArgTypes.
     */
    public static ArgTypes parse(String typeStr) {
        if (StringUtils.isEmpty(typeStr))
            return noArg;
        if (typeStr.startsWith(PREFIX))
            typeStr = typeStr.substring(PREFIX.length());
        if ("option".equals(typeStr)) {
            // FIXME ArgTypes.option is not defiend in Command.js
            typeStr = "value";
        }
        try {
            return ArgTypes.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            log.warn("ArgTypes.{} is not supported. Use ArgTypes.value instaed.", typeStr);
            return value;
        }
    }
}
