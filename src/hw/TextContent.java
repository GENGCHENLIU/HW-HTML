package hw;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents content in a document composed of text. This can be thought as the leaves in
 * a HW document tree.
 *
 * @version 1.0
 */
public abstract class TextContent implements Content<String> {

	protected List<String> lines = new ArrayList<>();

	protected TextContent(String content) { append(content); }

	@Override
	public void append(final String content) { lines.add(content); }

	public List<String> getLines() { return lines; }

	@Override
	public boolean isEmpty() { return lines.isEmpty(); }

	/**
	 * @return  the lines in this TextContent separated with newline characters
	 */
	@Override
	public String toString() {
		return String.join("\n", lines);
	}
}
