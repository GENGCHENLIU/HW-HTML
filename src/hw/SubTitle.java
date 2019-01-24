package hw;

import html.Content;
import html.Element;

/**
 * @version 1.0
 */
public final class SubTitle extends AbstractContent {
	public SubTitle(String title) {
		super(title);
	}

	@Override
	public Content toHtmlElement() {
		final Element element = new Element("h4");
		element.appendContent(getContent());
		return element;
	}
}
