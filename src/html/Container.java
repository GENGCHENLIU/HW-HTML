package html;

import java.util.Collection;
import java.util.List;

/**
 * A Container contains HTMLConvertible.
 * @version 1.0
 */
public interface Container {
	/**
	 * Appends the specified HTMLConvertible to this Container.
	 */
	void appendContent(Content content);

	/**
	 * Appends the specified text as a Text object.
	 */
	default void appendContent(String text) {
		appendContent(new Text(text));
	}

	/**
	 * Appends the specified Contents to this Container.
	 */
	default void appendContents(Collection<? extends Content> contents) {
		contents.forEach(this::appendContent);
	}

	/**
	 * Returns a List containing the Contents in this Container.
	 */
	List<Content> getContents();

	/**
	 * Finds the Element with the specified name in this and the children of this Container.
	 * If such Element does not exist, null is returned.
	 */
	default Element find(String name) {
		for (Content content : getContents()) {
			if (!(content instanceof Element)) continue;

			final Element element = (Element) content;

			Element result;
			if (element.getName().equals(name))
				result = element;
			else
				result = element.find(name);

			if (result != null)
				return result;
		}

		return null;
	}

	// consider using a properties object for finding tags
}
