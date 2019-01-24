package hw;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A document containing the title, author, contents and solutions of a homework assignment.
 * @version 1.2
 */
public final class HWDocument {
	private final String title, author;
	private final List<Content> contents = new ArrayList<>();

	/**
	 * Creates a new HWDocument with the specified arguments.
	 */
	public HWDocument(
			String title,
			String author,
			Collection<? extends Content> contents) {
		this.title = title;
		this.author = author;
		this.contents.addAll(contents);
	}

	public String getTitle() { return title; }
	public String getAuthor() { return author; }

	public List<Content> getContents() { return Collections.unmodifiableList(contents); }


	/**
	 * Parse a file to a HWDocument.
	 * @param file	a text file
	 */
	public static HWDocument parse(Path file) throws IOException {
		final Builder doc = new Builder();


		final List<String> lines = Files.lines(file).collect(Collectors.toList());

		int lineNum = 0;

		// look for title and author
		boolean hasTitle = false, hasAuthor = false;

		for (; lineNum < lines.size(); lineNum++) {
			final String line = lines.get(lineNum);

			if (line.isEmpty()) break;	// empty line, stop

			if (!hasTitle) {
				doc.setTitle(line);
				hasTitle = true;
			}
			else if (!hasAuthor) {
				doc.setAuthor(line);
				hasAuthor = true;
			}
		}


		// split into sections
		final List<List<String>> sections = new ArrayList<>();
		sections.add(new ArrayList<>());
		int lastSection = 0;

		boolean isLastLineEmpty = false;

		for (lineNum++; lineNum < lines.size(); lineNum++) {
			final String line = lines.get(lineNum);

			// 2 empty lines, new section
			if (isLastLineEmpty && line.isEmpty()) {
				removeMatchingTrailing(sections.get(lastSection), String::isEmpty);
				sections.add(new ArrayList<>());
				lastSection++;
			}
			else
				sections.get(lastSection).add(line);

			isLastLineEmpty = line.isEmpty();
		}

		// handle each section
		for (List<String> section : sections) {
			if (section.isEmpty()) continue;

			doc.addContent(new SubTitle(section.get(0)));

			boolean afterPreText = false;

			Paragraph paragraph = new Paragraph();
			doc.addContent(paragraph);
			for (int i = 1; i < section.size(); i++) {
				final String line = section.get(i);

				// if last line was pre text
				if (line.startsWith("//")) {
					if (afterPreText)
						doc.getContents().remove(paragraph);

					doc.addContent(new PreText(line.substring(2)));

					paragraph = new Paragraph();
					doc.addContent(paragraph);
				}
				else if (line.isEmpty() && !afterPreText) {
					paragraph = new Paragraph();
					doc.addContent(paragraph);
				}
				else
					paragraph.addLine(line);

				afterPreText = line.startsWith("//");
			}

			if (paragraph.getLines().isEmpty())	// empty paragraph, remove it
				doc.getContents().remove(paragraph);
		}


		return doc.build();
	}


	/**
	 * Traverse through the specified list in reverse and removes all elements matching
	 * the given predicate until the first element failing the condition if found.
	 * @param list	the list to traverse
	 * @param condition	the condition to check for
	 */
	private static <T> void removeMatchingTrailing(
			List<T> list,
			Predicate<? super T> condition) {
		for (int i = list.size()-1; i >= 0; i--) {
			if (!condition.test(list.get(i)))
				break;
			list.remove(i);
		}
	}


	public static final class Builder {
		private String title, author;
		private List<Content> contents = new ArrayList<>();

		public String getTitle() { return title; }
		public void setTitle(String title) { this.title = title; }

		public String getAuthor() { return author; }
		public void setAuthor(String author) { this.author = author; }

		public List<Content> getContents() { return contents; }
		public void addContent(Content content) { contents.add(content); }

		public HWDocument build() { return new HWDocument(title, author, contents); }
	}
}
