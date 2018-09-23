package jp.vmi.selenium.selenese.command;

/**
 * Default implementation for EndLoop.
 *
 * @deprecated use {@link EndBlockImpl} instead.
 */
@Deprecated
public abstract class EndLoopImpl extends EndBlockImpl {

    EndLoopImpl(int index, String name, String[] args, ArgumentType... argTypes) {
        super(index, name, args, argTypes);
    }
}
