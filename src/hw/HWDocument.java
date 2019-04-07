package hw;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A document containing the title, author, and structured into sections of subtitles and
 * paragraphs.
 */
public final class HWDocument {
	private final String title, author;
	private final List<Section> sections = new ArrayList<>();

	/**
	 * Creates a new HWDocument with the specified arguments.
	 */
	public HWDocument(
			String title,
			String author,
			Collection<? extends Section> sections) {
		this.title = title;
		this.author = author;
		this.sections.addAll(sections);
	}

	public String getTitle() { return title; }
	public String getAuthor() { return author; }

	public List<Section> getSections() { return Collections.unmodifiableList(sections); }


	/**
	 * Parse a file to a HWDocument.
	 * @param file	a text file
	 */
	public static HWDocument parse(Path file) throws IOException {
		final Builder doc = new Builder();


		final List<String> lines =
				Files.lines(file).collect(Collectors.toList());

		final Iterator<String> linesIt = lines.iterator();

		// look for title and author
		boolean hasTitle = false, hasAuthor = false;

		while (linesIt.hasNext()) {
			final String line = linesIt.next();

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

		while (linesIt.hasNext()) {
			final String line = linesIt.next();

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
		sections.stream()
				.map(Section::parse)
				.forEachOrdered(doc::addSection);

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
		private List<Section> sections = new ArrayList<>();

		public String getTitle() { return title; }
		public void setTitle(String title) { this.title = title; }

		public String getAuthor() { return author; }
		public void setAuthor(String author) { this.author = author; }

		public List<Section> getSections() { return sections; }
		public void addSection(Section section) { sections.add(section); }

		public HWDocument build() { return new HWDocument(title, author, sections); }
	}
}
