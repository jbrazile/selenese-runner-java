package jp.vmi.utils.beans.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Change property name.
 */
@Target({ FIELD, METHOD })
@Retention(RUNTIME)
public @interface Property {

    /**
     * @return changed property name.
     */
    String name() default "";
}
