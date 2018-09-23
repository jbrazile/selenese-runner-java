package jp.vmi.selenium.selenese.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Command sequence.
 */
public class CommandSequence {

    private static final char SEPARATOR = '-';

    private static class Counter {

        public StartBlock startBlock;
        public int count;

        public Counter(StartBlock startBlock, int count) {
            this.startBlock = startBlock;
            this.count = count;
        }
    }

    private final CommandSequence parent;
    private final List<Counter> counters = new ArrayList<>();
    private Counter tail;

    /**
     * Constructor.
     *
     * @param parent parent command sequence.
     */
    public CommandSequence(CommandSequence parent) {
        this.parent = parent;
        counters.add(tail = new Counter(StartBlock.NO_START_BLOCK, 0));
    }

    private static List<StartBlock> getListOfStartBlock(ICommand command) {
        List<StartBlock> result;
        StartBlock startBlock = command.getStartBlock();
        if (startBlock == StartBlock.NO_START_BLOCK)
            result = new ArrayList<>();
        else
            result = getListOfStartBlock((ICommand) startBlock);
        result.add(startBlock);
        return result;
    }

    /**
     * Increment sequence.
     *
     * @param command current command.
     */
    public void increment(ICommand command) {
        if (tail.startBlock == command.getStartBlock()) {
            tail.count++;
        } else {
            Iterator<Counter> citer = counters.iterator();
            ListIterator<StartBlock> siter = getListOfStartBlock(command).listIterator();
            int index = 0;
            while (citer.hasNext()) {
                if (!siter.hasNext())
                    break;
                if (citer.next().startBlock != siter.next()) {
                    siter.previous();
                    break;
                }
                index++;
            }
            if (index < counters.size())
                counters.subList(index, counters.size()).clear();
            counters.get(counters.size() - 1).count++;
            while (siter.hasNext()) {
                StartBlock s = siter.next();
                counters.add(new Counter(s, 1));
            }
            tail = counters.get(counters.size() - 1);
        }
        if (command instanceof StartBlock)
            counters.add(tail = new Counter((StartBlock) command, 1));
    }

    /**
     * Get level of sequences.
     *
     * @return level.
     */
    public int getLevel() {
        return (parent != null ? parent.getLevel() : 0) + counters.size();
    }

    private StringBuilder appendString(StringBuilder result) {
        if (result.length() > 0)
            result.append(SEPARATOR);
        Iterator<Counter> iter = counters.iterator();
        result.append(iter.next().count);
        while (iter.hasNext())
            result.append(SEPARATOR).append(iter.next().count);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(getLevel() * 4);
        if (parent != null)
            parent.appendString(result);
        return appendString(result).toString();
    }
}
