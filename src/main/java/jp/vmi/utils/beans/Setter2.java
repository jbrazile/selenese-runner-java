package jp.vmi.utils.beans;

/**
 * Setter function with return value.
 *
 * @param <T> type of target object.
 */
@FunctionalInterface
public interface Setter2<T> {

    /**
     * Set value.
     *
     * @param object target object.
     * @param value value for setting.
     * @return return value.
     */
    Object set(T object, Object value);
}
