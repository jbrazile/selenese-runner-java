package jp.vmi.selenium.selenese.command;

import java.util.Arrays;
import java.util.List;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.SourceType;
import jp.vmi.selenium.selenese.VarsMap;
import jp.vmi.selenium.selenese.locator.Locator;
import jp.vmi.selenium.selenese.result.Result;

/**
 * Command interface.
 */
public interface ICommand {

    /**
     * Get source elements.
     *
     * @return array of source elements. (always 3 elements)
     */
    String[] getSource();

    /**
     * Get index in selenese script file (1 origin).
     *
     * @return index.
     */
    int getIndex();

    /**
     * Get command arguments.
     *
     * @return command arguments.
     */
    String[] getArguments();

    /**
     * Get command arguments where variables resolved.
     *
     * @param sourceType source type.
     * @param varsMap map of variables.
     * @return arguments.
     */
    default String[] getVariableResolvedArguments(SourceType sourceType, VarsMap varsMap) {
        return Arrays.stream(getArguments()).map(arg -> varsMap.replaceVars(false, arg)).toArray(String[]::new);
    }

    /**
     * Get command name.
     *
     * @return command name.
     */
    String getName();

    /**
     * Convert locators from arguments.
     *
     * @param args the command arguments.
     * @return converted locators.
     */
    @Deprecated
    String[] convertLocators(String[] args);

    /**
     * Extract locators from arguments.
     *
     * @param args the command arguments.
     * @return extracted parsed locators.
     */
    default Locator[] extractLocators(String[] args) {
        return Arrays.stream(convertLocators(args)).map(Locator::new).toArray(Locator[]::new);
    }

    /**
     * May the command update screen?
     *
     * @return true if the command may update screen.
     */
    boolean mayUpdateScreen();

    /**
     * Execute the command.
     * <p>
     * Note: set the command result to testCase in this method.
     * </p>
     *
     * @param context Selenese Runner context.
     * @param curArgs current arugments. (the variables in it are extracted)
     * @return the command result.
     */
    Result execute(Context context, String... curArgs);

    /**
     * Get the command result.
     *
     * @return the command result.
     */
    Result getResult();

    /**
     * Set beginning-of-block command.
     *
     * @param startBlock beginning-of-block command.
     */
    void setStartBlock(StartBlock startBlock);

    /**
     * Get beginning-of-block command.
     *
     * @return beginning-of-block command.
     */
    StartBlock getStartBlock();

    /**
     * Set beginning-of-loop command.
     *
     * @param startLoop beginning-of-loop command.
     *
     * @deprecated use {@link #setStartBlock(StartBlock)} instaed.
     */
    @Deprecated
    void setStartLoop(StartLoop startLoop);

    /**
     * Get beginning-of-loop command.
     *
     * @return beginning-of-loop command.
     *
     * @deprecated use {@link #getStartBlock()} instaed.
     */
    @Deprecated
    StartLoop getStartLoop();

    /**
     * Add screenshot image.
     *
     * @param path image path.
     * @param label image label.
     */
    void addScreenshot(String path, String label);

    /**
     * Get list of screenshot images.
     *
     * @return list of image path of sreenshot.
     */
    List<Screenshot> getScreenshots();
}
