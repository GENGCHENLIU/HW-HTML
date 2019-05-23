package hw;

import html.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * A logical grouping of contents. A group is itself a content.
 */
public abstract class Group implements Content<Content<?>> {

	protected List<Content<?>> contents = new ArrayList<>();

	@Override
	public void append(Content<?> content) { contents.add(content); }
	public List<Content<?>> getContents() { return contents; }

	@Override
	public boolean isEmpty() { return contents.isEmpty(); }

	/**
	 * @return  element form of all Contents stored in this Group bundled into a
	 * &lt;div&gt; element
	 */
	@Override
	public Element toHtmlContent() {
		final Element div = new Element("div");
		contents.forEach(content -> div.appendContent(content.toHtmlContent()));
		return div;
	}
}
