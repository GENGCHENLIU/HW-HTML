package hw;

import html.BR;
import html.Element;

import java.util.Iterator;

/**
 * Regular text in a document.
 */
public class Text extends TextContent {
	public Text(String text) {
		super(text);
	}

	@Override
	public html.Content toHtmlContent() {
		final Element p = new Element("p");

		final Iterator<String> linesIt = getLines().iterator();
		if (linesIt.hasNext())
			p.appendContent(linesIt.next());

		linesIt.forEachRemaining(line -> {
			p.appendContent(BR.INSTANCE);
			p.appendContent(line);
		});

		return p;
	}
}
