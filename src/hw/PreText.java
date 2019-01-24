package hw;

import html.Content;
import html.Text;

/**
 * PreText objects are pre-formatted and should be treated as is.
 * @version 1.0
 */
public class PreText extends AbstractContent {

	public PreText(String text) {
		super(text + '\n');
	}

	@Override
	public Content toHtmlElement() {
		return new Text(getContent());
	}
}
