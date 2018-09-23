package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "times".
 */
public class Times extends StartBlockImpl {

    private static final int ARG_TIMES = 0;

    private boolean isStarted = false;
    private int times;
    private int count;

    Times(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        if (isStarted) {
            if (count++ > times) {
                isStarted = false;
                context.getCommandListIterator().jumpToNextOf(endBlock);
                return new Success("Finished " + times + " times repitition");
            }
        } else {
            times = context.executeScript(curArgs[ARG_TIMES]);
            if (times <= 0) {
                context.getCommandListIterator().jumpToNextOf(endBlock);
                return new Success("Skip: times is " + times);
            }
            isStarted = true;
            count = 1;
        }
        return new Success(String.format("Times: %d/%d", count, times));
    }
}
