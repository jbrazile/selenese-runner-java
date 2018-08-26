package jp.vmi.utils.json;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class JsonParserTest {

    public static class BeanA {
        public long a;
    }

    @Test
    public void test() {
        StringReader r;

        r = new StringReader("\"abc\"");
        String r1 = JsonParser.parse(r, String.class);
        assertThat(r1, is("abc"));

        r = new StringReader("123");
        Number r2 = JsonParser.parse(r, Number.class);
        assertThat(r2, is(123L));

        r = new StringReader("{}");
        Map<?, ?> r3 = JsonParser.parse(r, Map.class);
        assertThat(r3.isEmpty(), is(true));

        r = new StringReader("[]");
        List<?> r4 = JsonParser.parse(r, List.class);
        assertThat(r4.isEmpty(), is(true));

        r = new StringReader("{\"a\":1}");
        Map<?, ?> r5 = JsonParser.parse(r, Map.class);
        assertThat(r5.size(), is(1));
        assertThat(r5.get("a"), is(1L));
    }
}
