package jp.vmi.utils.beans;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class BeanMapTest {

    public static class Bean00 {
    }

    public static class Bean01 {
        public String s = "ABC";
    }

    public static class Bean02 {
        public String s = "ABC";

        public String getS() {
            return s;
        }
    }

    public static class Bean03 {
        public String s = "ABC";

        public void setS(String s) {
            this.s = s;
        }
    }

    public static class Bean03_02 {
        public String s = "ABC";

        public String setS(String s) {
            return this.s = s;
        }
    }

    public static class Bean04 {
        public String s = "ABC";

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }
    }

    public static class Bean05 {
        public final String s = "ABC";
    }

    public static class Bean11 {
        private String s = "ABC";

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }
    }

    public static class Bean12 {
        private final String s = "ABC";

        public String getS() {
            return s;
        }
    }

    public static class Bean13 {
        @SuppressWarnings("unused")
        private String s = "ABC";

        public void setS(String s) {
            this.s = s;
        }
    }

    @Test
    public void test() {
        BeanMap<Object, String, String> bm;
        bm = new BeanMap<>(new Bean00());
        assertThat(bm.size(), is(0));
        for (Object o : new Object[] {
            new Bean01(),
            new Bean02(),
            new Bean03(),
            new Bean03_02(),
            new Bean04(),
            new Bean05(),
            new Bean11(),
            new Bean12(),
        }) {
            System.out.println(o.getClass());
            bm = new BeanMap<>(o);
            assertThat(bm.containsKey("s"), is(true));
            assertThat(bm.get("s"), is("ABC"));
            boolean checked = false;
            try {
                bm.put("s", "abc");
            } catch (UnsupportedOperationException e) {
                switch (o.getClass().getSimpleName()) {
                case "Bean05":
                case "Bean12":
                    checked = true;
                    break;
                default:
                    throw e;
                }
            }
            if (!checked)
                assertThat(bm.get("s"), is("abc"));
        }
        bm = new BeanMap<>(new Bean13());
        assertThat(bm.isEmpty(), is(true));
    }
}
