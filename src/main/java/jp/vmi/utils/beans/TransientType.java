package jp.vmi.utils.beans;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;

import jp.vmi.utils.beans.annotation.Transient;

enum TransientType {
    // with @Transient, @Transient(true) or transient modifier.
    TRUE,
    // with @Transient(false)
    FALSE,
    // without @Transient nor transient modifier.
    NONE;

    public TransientType updatedBy(TransientType other) {
        return other == NONE ? this : other;
    }

    public TransientType updatedBy(AnnotatedElement elem, int mod) {
        return updatedBy(geTransientType(elem, mod));
    }

    public static TransientType geTransientType(AnnotatedElement elem, int mod) {
        Transient t = elem.getAnnotation(Transient.class);
        if (t != null)
            return t.value() ? TRUE : FALSE;
        java.beans.Transient t2 = elem.getAnnotation(java.beans.Transient.class);
        if (t2 != null)
            return t2.value() ? TRUE : FALSE;
        return Modifier.isTransient(mod) ? TRUE : NONE;
    }
}
