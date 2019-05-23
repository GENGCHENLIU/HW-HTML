package hw;

import html.Attribute;
import html.Element;

/**
 * A Paragraph is similar to Group, except that the HTML representation is marked with
 * a special class called "paragraph".
 */
public final class Paragraph extends Group {
	@Override
	public Element toHtmlContent() {
		final Element element =
				new Element("div", new Attribute("class", "paragraph"));

		contents.forEach(content -> element.appendContent(content.toHtmlContent()));

		return element;
	}
}
