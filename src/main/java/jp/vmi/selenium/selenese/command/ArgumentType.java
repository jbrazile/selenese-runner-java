package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.parser.ArgTypes;

import static jp.vmi.selenium.selenese.parser.ArgTypes.*;

/**
 * Type of command arguments.
 */
public enum ArgumentType {
    /** Value not locator */
    VALUE(value),

    /** Locator */
    LOCATOR(locator),

    /** Attribute locator (with '@attribute_name') */
    ATTRIBUTE_LOCATOR(attributeLocator),

    /** CSS locator (implies "css=") */
    CSS_LOCATOR(cssLocator),

    /** Option locator */
    OPTION_LOCATOR(optionLocator),

    /* end of enum list */;

    /** Argument type of Selenium IDE TNG */
    public final ArgTypes argType;

    ArgumentType(ArgTypes argType) {
        this.argType = argType;
    }

}
