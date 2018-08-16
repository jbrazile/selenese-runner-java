package jp.vmi.utils.beans.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * If true, it is excluded from BeanMap processing as if {@code transient} modifier is given.
 * If false, it is included in BeanMap processing even if {@code transient} modifier is given.
 */
@Target({ FIELD, METHOD })
@Retention(RUNTIME)
public @interface Transient {

    @SuppressWarnings("javadoc")
    boolean value() default true;
}
