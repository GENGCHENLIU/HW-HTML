package html;

import java.util.*;

/**
 * A HTML tag.
 * @version 1.0
 */
public class Element implements Container, Content {
	private final String name;
	private final List<Attribute> attributes = new ArrayList<>();
	private final List<Content> contents = new ArrayList<>();

	/**
	 * Creates a new Element with the specified name.
	 */
	public Element(String name) {
		this.name = name;
	}

	/**
	 * Creates a new Element with the specified name and attributes.
	 */
	public Element(String name, Attribute... attributes) {
		this.name = name;
		// would use List.of, but not used for java 8 compatibility
		this.attributes.addAll(Arrays.asList(attributes));
	}


	public String getName() { return name; }

	public List<Attribute> getAttributes() {
		return Collections.unmodifiableList(attributes);
	}

	@Override
	public void appendContent(Content content) { contents.add(content); }

	@Override
	public List<Content> getContents() { return contents; }


	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		// open tag
		builder.append('<').append(getName());
		// attributes
		attributes.forEach(attr -> builder.append(' ').append(attr));
		builder.append('>');
		if (!contents.isEmpty())
			builder.append('\n');

		// content
		contents.forEach(builder::append);

		// close tag
		builder.append('\n');
		builder.append("</").append(getName()).append('>');
		builder.append('\n');

		return builder.toString();
	}
}
