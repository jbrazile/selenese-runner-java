package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "repeatIf".
 */
public class RepeatIf extends AbstractCommand implements EndBlock {

    private static final int ARG_CONDITION = 0;

    RepeatIf(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        if (context.isTrue(curArgs[ARG_CONDITION])) {
            StartBlock startBlock = getStartBlock();
            context.getCommandListIterator().jumpTo(startBlock);
            return new Success("Repeat");
        } else {
            return new Success("Break");
        }
    }
}
