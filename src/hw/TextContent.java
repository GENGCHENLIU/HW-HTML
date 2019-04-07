package hw;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 */
public abstract class TextContent implements HTMLConvertible {
	protected final List<String> lines = new ArrayList<>();

	protected TextContent(String content) {
		lines.add(content);
	}

	public void append(String more) {
		lines.add(more);
	}

	public List<String> getLines() { return lines; }

	/**
	 * @return  the lines in this TextContent separated with newline characters
	 */
	@Override
	public String toString() {
		return String.join("\n", lines);
	}
}
