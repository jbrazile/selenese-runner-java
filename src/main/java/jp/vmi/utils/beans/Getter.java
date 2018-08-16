package jp.vmi.utils.beans;

/**
 * Getter function.
 *
 * @param <T> type of target object.
 */
@FunctionalInterface
public interface Getter<T> {

    /**
     * Get value.
     *
     * @param object target object.
     * @return value.
     */
    Object get(T object);
}
