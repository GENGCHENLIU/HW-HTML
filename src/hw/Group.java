package hw;

import html.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * A logical grouping of contents.
 */
public abstract class Group implements HTMLConvertible {
	protected List<HTMLConvertible> contents = new ArrayList<>();

	public void append(HTMLConvertible content) { contents.add(content); }
	public List<HTMLConvertible> getContents() { return contents; }

	public boolean isEmpty() { return contents.isEmpty(); }

	/**
	 * @return  html.Element form of all Contents stored in this Group bundled into a
	 * &lt;div&gt; element
	 */
	@Override
	public html.Element toHtmlContent() {
		final Element div = new Element("div");
		contents.forEach(content -> div.appendContent(content.toHtmlContent()));
		return div;
	}
}
