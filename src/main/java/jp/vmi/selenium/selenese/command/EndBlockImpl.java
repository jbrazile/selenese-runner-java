package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Default implementation for EndLoop.
 */
public abstract class EndBlockImpl extends AbstractCommand implements EndBlock {

    EndBlockImpl(int index, String name, String[] args, ArgumentType... argTypes) {
        super(index, name, args, argTypes);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        StartBlock startBlock = getStartBlock();
        context.getCommandListIterator().jumpTo(startBlock);
        return SUCCESS;
    }
}
