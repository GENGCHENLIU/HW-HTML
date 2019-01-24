package hw;

/**
 * @version 1.0
 */
public abstract class AbstractContent implements Content {
	private final String content;

	public AbstractContent(String content) {
		this.content = content;
	}

	public String getContent() { return content; }
}
