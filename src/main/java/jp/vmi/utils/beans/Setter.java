package jp.vmi.utils.beans;

/**
 * Setter function.
 *
 * @param <T> type of target object.
 */
@FunctionalInterface
public interface Setter<T> {

    /**
     * Set value.
     *
     * @param object target object.
     * @param value value for setting.
     */
    void set(T object, Object value);
}
