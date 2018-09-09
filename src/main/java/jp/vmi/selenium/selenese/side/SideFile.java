package jp.vmi.selenium.selenese.side;

import java.util.ArrayList;
import java.util.List;

/**
 * element of side format file.
 */
@SuppressWarnings("javadoc")
public class SideFile extends SideBase {

    private String url;
    private List<SideTest> tests;
    private List<SideSuite> suites;
    private List<Object> urls;
    private List<Object> plugins;

    public SideFile() {
        this(false);
    }

    public SideFile(boolean isGen) {
        super(isGen);
        if (isGen) {
            tests = new ArrayList<>();
            suites = new ArrayList<>();
            urls = new ArrayList<>();
            plugins = new ArrayList<>();
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<SideTest> getTests() {
        return tests;
    }

    public void setTests(List<SideTest> tests) {
        this.tests = tests;
    }

    public List<SideSuite> getSuites() {
        return suites;
    }

    public void setSuites(List<SideSuite> suites) {
        this.suites = suites;
    }

    public List<Object> getUrls() {
        return urls;
    }

    public void setUrls(List<Object> urls) {
        this.urls = urls;
    }

    public List<Object> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<Object> plugins) {
        this.plugins = plugins;
    }

    public void addTest(SideTest test) {
        tests.add(test);
    }

    public void addSuite(SideSuite suite) {
        suites.add(suite);
    }
}
