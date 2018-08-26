package jp.vmi.selenium.selenese.side;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.BiFunction;

import org.openqa.selenium.json.JsonInput;
import org.openqa.selenium.json.PropertySetting;
import org.openqa.selenium.json.TypeCoercer;

/**
 * element of side format file.
 */
@SuppressWarnings("javadoc")
public class SideFile extends SideBase {

    private static class SideFileCoercer extends TypeCoercer<SideFile> {

        @Override
        public boolean test(Class<?> aClass) {
            return aClass == SideFile.class;
        }

        @Override
        public BiFunction<JsonInput, PropertySetting, SideFile> apply(Type type) {
            return (jsonInput, propertySetting) -> {
                SideFile sideFile = new SideFile();
                jsonInput.beginObject();
                while (jsonInput.hasNext()) {
                    String name = jsonInput.nextName();
                    switch (jsonInput.peek()) {
                    case BOOLEAN:
                        break;
                    case NAME:
                        break;
                    case NULL:
                        break;
                    case NUMBER:
                        break;
                    case START_MAP:
                        break;
                    case END_MAP:
                        break;
                    case START_COLLECTION:
                        break;
                    case END_COLLECTION:
                        break;
                    case STRING:
                        break;
                    case END:
                        break;
                    }
                    switch (name) {
                    case "url":
                        break;
                    case "tests":
                        break;
                    case "suites":
                        break;
                    case "urls":
                        break;
                    default:
                        break;
                    }
                }
                jsonInput.endObject();
                return sideFile;
            };
        }
    }

    public static TypeCoercer<SideFile> newTypeCoercer() {
        return new SideFileCoercer();
    }

    private String url;
    private List<SideTest> tests;
    private List<SideSuite> suites;
    private List<Object> urls;

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
}
