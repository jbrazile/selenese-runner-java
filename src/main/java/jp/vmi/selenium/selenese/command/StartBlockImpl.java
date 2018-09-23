package jp.vmi.selenium.selenese.command;

/**
 * Default implementation for StartBlock.
 */
public abstract class StartBlockImpl extends AbstractCommand implements StartBlock {

    protected EndBlock endBlock;

    StartBlockImpl(int index, String name, String[] args, ArgumentType... argTypes) {
        super(index, name, args, argTypes);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    public void setEndBlock(EndBlock endBlock) {
        this.endBlock = endBlock;
    }
}
