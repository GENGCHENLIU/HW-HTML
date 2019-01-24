package html;

/**
 * Represents an attribute on a Element.
 * @version 1.0
 */
public class Attribute {
	private final String name, value;

	public Attribute(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.format("%s=\"%s\"", name, value);
	}
}
