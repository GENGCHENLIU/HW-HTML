package hw;

import html.Attribute;
import html.Element;

/**
 * A Paragraph is a logical grouping
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
