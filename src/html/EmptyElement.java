package html;

import java.util.Collection;
import java.util.List;

/**
 * An EmptyElement has no content.
 * @version 1.0
 */
public class EmptyElement extends Element {

	public EmptyElement(String name) { super(name); }
	public EmptyElement(String name, Attribute... attributes) {
		super(name, attributes);
	}

	@Override
	public void appendContent(Content content) { throw new UnsupportedOperationException(); }
	@Override
	public void appendContents(Collection<? extends Content> contents) { throw new UnsupportedOperationException(); }
	@Override
	public List<Content> getContents() { throw new UnsupportedOperationException(); }

	@Override
	public String toString() {
		if (getAttributes().isEmpty())
			return '<' + getName() + '>';
		else {
			final StringBuilder builder = new StringBuilder();

			// open tag
			builder.append('<').append(getName());
			// attributes
			getAttributes().forEach(attr -> builder.append(' ').append(attr));
			builder.append('>');

			return builder.toString();
		}
	}
}
