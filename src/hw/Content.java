package hw;

/**
 * The most generic type for content in a HW document.
 */
public interface Content<T> extends HTMLConvertible {
	void append(T content);
	boolean isEmpty();
}
