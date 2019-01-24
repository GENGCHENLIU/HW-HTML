import hw.*;
import html.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * @version 2.7
 */
public class HWtoHTML {
	private static final String VERSION = "2.7";


	private static final String DEFAULT_CSS_FILE = "default.css";

	// The default CSS
	private static final String DEFAULT_CSS = readFile(DEFAULT_CSS_FILE);

	/**
	 * Reads all lines from the specified file, returns the content of the file as a
	 * single string, preserving line breaks as newlines.
	 * @param path  the path to the file to be read
	 * @return  a string containing all lines of the specified file
	 */
	private static String readFile(final String path) {
		try (final Stream<String> lines = Files.lines(Paths.get(path))) {
			return
					lines.collect(
							StringBuilder::new,
							// preserve line breaks
							(stringBuilder, line) -> stringBuilder.append(line).append('\n'),
							StringBuilder::append)
					.toString();
		}
		catch (IOException e) {
			System.err.printf("Failed to read '%s'%n", path);
			return BACKUP_CSS;
		}
	}


	// a backup if default css file failed to read
	private static final String BACKUP_CSS = ".center {\n" +
											  "\tmargin-left: auto;\n" +
											  "\tmargin-right: auto;\n" +
											  "}\n" +
											  "\n" +
											  "body, p {\n" +
											  "\tmax-width: 700px;\n" +
											  "\tmargin-left: auto;\n" +
											  "\tmargin-right: auto;\n" +
											  "}\n" +
											  "\n" +
											  ".strictCenter {\n" +
											  "\tmax-width: 700px;\n" +
											  "\tmargin-left: auto;\n" +
											  "\tmargin-right: auto;\n" +
											  "\ttext-align: center;\n" +
											  "}\n" +
											  "\n" +
											  "@media print {\n" +
											  "\tdiv { page-break-inside: avoid; }\n" +
											  "}\n" +
											  "\n" +
											  "p { line-height: 1.5; }\n\n";


	public static void main(String... args) {


		if (args.length > 0 && args[0].equals("--print-default")) {
			System.out.print(DEFAULT_CSS);
			return;
		}
		else if (args.length > 0 && args[0].equals("--print-backup")) {
			System.out.print(BACKUP_CSS);
			return;
		}
		else if (args.length < 2 || (args[0].equals("--clean") && args.length < 3)) {
			System.out.println("Version: " + VERSION);
			System.out.println("Usage:");
			System.out.println("HWtoHTML <input> <output> [css]");
			System.out.println("HWtoHTML --clean <input> <output> [css]");
			System.out.println("HWtoHTML --print-default");
			System.out.println("HWtoHTML --print-backup");

			return;
		}


		final boolean noDefaultCSS = args[0].equals("--clean");
		final String baseCSS = noDefaultCSS ? "" : DEFAULT_CSS;

		/*
		target file
		output file
		 */
		final String input, output, cssFile;
		if (noDefaultCSS) {
			input = args[1];
			output = args[2];

			if (args.length >= 4)
				cssFile = args[3];
			else cssFile = null;
		}
		else {
			input = args[0];
			output = args[1];

			if (args.length >= 3)
				cssFile = args[2];
			else cssFile = null;
		}


		// read HW doc
		HWDocument hwDoc = null;
		try {
			hwDoc = HWDocument.parse(Paths.get(input));
		}
		catch (IOException e) {
			System.err.println("Failed to read file: " + input);
			e.printStackTrace();
		}

		if (hwDoc == null) throw new IllegalStateException();



		// translate to HTML
		// read css, prepare html file
		HtmlDocument htmlDoc;
		if (cssFile == null)
			htmlDoc = newHTMLDocForHW(baseCSS);
		else {
			try {
				htmlDoc = newHTMLDocForHW(baseCSS + String.join("\n", Files.readAllLines(Paths.get(cssFile))));
			}
			catch (IOException e) {
				System.err.printf("Failed to read css file '%s'%n", cssFile);
				htmlDoc = newHTMLDocForHW(baseCSS);
			}
		}


		final Element body = new Element("body");

		{
			// title
			if (hwDoc.getTitle() != null) {
				final Element title =
						new Element("h1",
								new Attribute("class", "strictCenter"),
								new Attribute("id", "title"));
				title.appendContent(hwDoc.getTitle());
				body.appendContent(title);
			}
		}

		{
			// author
			if (hwDoc.getAuthor() != null) {
				final Element author =
						new Element("h3",
								new Attribute("class", "strictCenter"),
								new Attribute("id", "author"));
				author.appendContent(hwDoc.getAuthor());
				body.appendContent(author);
			}
		}

		{
			// contents, typically questions and solutions
			int i = 0;  // index the <div>s to make them easier to refer to in CSS

			Element div = new Element("div",
					new Attribute("class", "contentDiv"),
					new Attribute("id", "content"+ (i++)));

			body.appendContent(div);
			for (hw.Content content : hwDoc.getContents()) {
				// if the content is a SubTitle (i.e. <h4>) and the current one has stuff in it
				if (content instanceof SubTitle && !div.getContents().isEmpty()) {
					// creates a new <div> and puts subsequent contents in that <div>
					div = new Element("div",
							new Attribute("class", "contentDiv"),
							new Attribute("id", "content"+ (i++)));
					body.appendContent(div);
				}

				div.appendContent(content.toHtmlElement());
			}
		}

		htmlDoc.appendContent(body);


		// write HTML doc
		try {
			Files.write(Paths.get(output), Collections.singletonList(htmlDoc.toString()));
		}
		catch (IOException e) {
			System.err.println("Failed to write file: " + output);
			e.printStackTrace();
		}
	}


	/**
	 * Creates a new HtmlDocument specifically for HWDocuments.
	 */
	private static HtmlDocument newHTMLDocForHW(final String css) {
		final HtmlDocument doc = new HtmlDocument();

		final Element head = new Element("head");

		// add script
		final String scriptSrc =
				"https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.4/latest.js?config=AM_CHTML";
		head.appendContent(
				new Element("script", new Attribute("src", scriptSrc)));


		// if there is some CSS, put it in <head>
		if (css != null && !css.isEmpty()) {
			final Element style = new Element("style");
			style.appendContent(css);
			head.appendContent(style);
		}


		doc.appendContent(head);

		return doc;
	}
}
