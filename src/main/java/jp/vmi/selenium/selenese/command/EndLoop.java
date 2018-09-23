package jp.vmi.selenium.selenese.command;

/**
 * interface for end-of-loop commands.
 *
 * @deprecated use {@link EndBlock} instaed.
 */
@Deprecated
public interface EndLoop extends EndBlock {

    /**
     * Set beginning-of-loop command.
     *
     * @param startLoop beginning-of-loop command.
     *
     * @deprecated use {@link EndBlock} instead.
     */
    @Deprecated
    void setStartLoop(StartLoop startLoop);
}
