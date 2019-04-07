package hw;

import java.util.Iterator;

/**
 * A section is a logical grouping of contents.
 */
public final class Section extends Group {

	public static Section parse(Iterable<? extends String> lines) {
		final Iterator<? extends String> linesIt = lines.iterator();
		final Section section = new Section();

		if (!linesIt.hasNext())  // empty section
			return section;

		// first line subtitle
		section.append(new SubTitle(linesIt.next()));

		/*
		For each line, add to existing content if able to, otherwise put old content in
		paragraph, create new content, and add line to that. Add paragraph to section and
		create new paragraph if blank line is encountered.
		 */
		Paragraph paragraph = null;
		TextContent content = null;

		// for each remaining line
		while (linesIt.hasNext()) {
			final String line = linesIt.next();

			// init on first line
			if (paragraph == null)
				paragraph = new Paragraph();
			// empty line, new paragraph
			else if (line.isEmpty()) {
				if (content != null && !content.getLines().isEmpty()) {
					paragraph.append(content);
					content = null;
				}
				if (!paragraph.isEmpty()) {
					section.append(paragraph);
					paragraph = new Paragraph();
				}
				continue;
			}


			// pre text
			if (line.startsWith("//")) {
				if (content == null) // handle null
					content = new PreText(line.substring(2));
				else {
					// if type of content is or is subtype of PreText
					if (PreText.class.isAssignableFrom(content.getClass()))
						// append to that
						content.append(line.substring(2));
					else {
						// put content in paragraph and prepare new content
						paragraph.append(content);
						content = new PreText(line.substring(2));
					}
				}
			}

			// regular text
			else {
				if (content == null) // handle null
					content = new Text(line);
				else {
					// if type of content is or is subtype of Text
					if (Text.class.isAssignableFrom(content.getClass()))
						// append to that
						content.append(line);
					else {
						// put content in paragraph and prepare new content
						paragraph.append(content);
						content = new Text(line);
					}
				}
			}
		}

		// if paragraph is null, no line after subtitle
		if (paragraph != null) {
			if (content != null && !content.getLines().isEmpty())
				paragraph.append(content);
			if (!paragraph.isEmpty())
				section.append(paragraph);
		}

		return section;
	}
}
