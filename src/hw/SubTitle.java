package hw;

import html.Element;

public final class SubTitle extends TextContent {
	public SubTitle(String title) {
		super(title);
	}

	@Override
	public html.Content toHtmlContent() {
		final Element element = new Element("h4");
		element.appendContent(toString());
		return element;
	}
}
