package hw;

import html.Content;
import html.Text;

/**
 * Tre-formatted text that is treated as is.
 */
public class PreText extends TextContent {

	public PreText(String text) {
		super(text);
	}

	@Override
	public Content toHtmlContent() {
		return new Text(toString());
	}
}
