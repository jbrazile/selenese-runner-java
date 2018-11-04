package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.FlowControlState;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "times".
 */
public class Times extends StartBlockImpl {

    private static final int ARG_TIMES = 0;

    private static class TimesState implements FlowControlState {

        final int times;
        int count = 1;

        TimesState(int times) {
            this.times = times;
        }
    }

    Times(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        TimesState state = context.getFlowControlState(this);
        if (state != null) {
            if (++state.count > state.times) {
                context.setFlowControlState(this, null);
                context.getCommandListIterator().jumpToNextOf(endBlock);
                return new Success("Finished " + state.times + " times repitition");
            }
        } else {
            int times = context.executeScript(curArgs[ARG_TIMES]);
            if (times <= 0) {
                context.getCommandListIterator().jumpToNextOf(endBlock);
                return new Success("Skip: times is " + times);
            }
            state = new TimesState(times);
            context.setFlowControlState(this, state);
        }
        return new Success(String.format("Times: %d/%d", state.count, state.times));
    }
}
