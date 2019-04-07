package html;

import java.util.Collections;
import java.util.List;

/**
 * Represents a &lt;br&gt; element.
 * Because the &lt;br&gt; element has no content or attribute, it has only one state.
 * Therefore, this class provides a singleton instance and cannot be constructed.
 */
public final class BR extends EmptyElement {

	public static final BR INSTANCE = new BR();

	private BR() {
		super("br");
	}

	@Override
	public void addAttribute(Attribute attribute) {
		throw new UnsupportedOperationException();
	}
	@Override
	public List<Attribute> getAttributes() { return Collections.emptyList(); }
}
