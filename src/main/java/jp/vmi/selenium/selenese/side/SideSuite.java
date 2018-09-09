package jp.vmi.selenium.selenese.side;

import java.util.ArrayList;
import java.util.List;

/**
 * "suite" element of side format.
 */
@SuppressWarnings("javadoc")
public class SideSuite extends SideBase {

    private boolean parallel;

    private long timeout;

    // list of uuid of test.
    private List<String> tests;

    public SideSuite() {
        this(false);
    }

    public SideSuite(boolean isGen) {
        super(isGen);
        if (isGen) {
            parallel = false;
            timeout = 300;
            tests = new ArrayList<>();
        }
    }

    public boolean isParallel() {
        return parallel;
    }

    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public List<String> getTests() {
        return tests;
    }

    public void setTests(List<String> tests) {
        this.tests = tests;
    }

    public void addTest(SideTest sideTest) {
        tests.add(sideTest.getId());
    }
}
