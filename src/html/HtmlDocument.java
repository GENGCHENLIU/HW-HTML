package html;

import java.util.List;

/**
 * Represents a HTML document. This implementation is not meant to comply to any standard.
 * @version 1.0
 */
public final class HtmlDocument implements Container {
	private static final EmptyElement DOCTYPE = new EmptyElement("!DOCTYPE html");
	private final Element root = new Element("html");

	public void appendContent(Content content) {
		root.appendContent(content);
	}

	public List<Content> getContents() {
		return root.getContents();
	}


	@Override
	public String toString() {
		return DOCTYPE.toString() + '\n' + root.toString();
	}
}
