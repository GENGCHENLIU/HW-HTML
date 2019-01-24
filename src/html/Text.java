package html;

/**
 * Represents literal text.
 * @version 1.0
 */
public class Text implements Content {
	private final String text;

	public Text(String text) {
		this.text = text;
	}

	@Override
	public String toString() { return text; }
}
