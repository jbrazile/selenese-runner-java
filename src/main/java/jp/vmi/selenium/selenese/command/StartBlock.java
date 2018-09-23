package jp.vmi.selenium.selenese.command;

/**
 * Interface for beginning-of-block commands.
 */
public interface StartBlock {

    /** Use NO_START_LOOP instaed of null. */
    public static final StartBlock NO_START_BLOCK = new StartBlock() {

        @Override
        public void setEndBlock(EndBlock endBlock) {
            // no operation.
        }

        @Override
        public String toString() {
            return "NO_START_BLOCK";
        }
    };

    /** The separator of reached counts. */
    public static final String REACHED_COUNT_SEPARATOR = "-";

    /**
     * Set end-of-block command.
     *
     * @param endBlock end-of-block command.
     */
    void setEndBlock(EndBlock endBlock);

    /**
     * true if the next block is skipped.
     *
     * @return true if the next block is skipped..
     */
    default boolean isSkippedNextBlock() {
        return false;
    }
}
