package hw;

import html.BR;
import html.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @version 1.0
 */
public class Paragraph implements Content {
	private final List<String> lines = new ArrayList<>();

	public void addLine(String line) { lines.add(line); }
	public List<String> getLines() { return lines; }

	@Override
	public html.Content toHtmlElement() {
		final Element element = new Element("p");

		for (Iterator<String> it = lines.iterator(); it.hasNext();) {
			final String line = it.next();

			element.appendContent(line);
			if (it.hasNext()) {
				element.appendContent(new BR());
				element.appendContent("\n");
			}
		}

		return element;
	}
}
