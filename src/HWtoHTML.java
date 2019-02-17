import hw.*;
import html.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * @version 2.8
 */
public class HWtoHTML {
	private static final String VERSION = "2.8";


	private static class PropertiesUtils {
		/**
		 * Loads the properties file from the specified stream.
		 * @param inStream  input stream of the properties file
		 * @param defaults  default values, may be null
		 * @return a new Properties loaded with the content of the specified stream, null if
		 * failed to read
		 */
		private static Properties loadProperties(
				final InputStream inStream,
				final Properties defaults) {
			try {
				final Properties properties = new Properties(defaults);
				properties.load(inStream);
				return properties;
			}
			catch (IOException e) {
				System.err.println("Failed to load properties");
				e.printStackTrace();
				return null;
			}
		}

		private static Properties loadProperties(
				final String path,
				final Properties defaults) {
			try (final InputStream propertiesFileStream =
					     Files.newInputStream(Paths.get(path))) {
				return loadProperties(propertiesFileStream, defaults);
			}
			catch (IOException e) {
				System.err.printf("Failed to read '%s'%n", path);
				e.printStackTrace();
				return null;
			}
		}

		/**
		 * Gets from the specified Properties. Note 'null safe' refers to properties being
		 * nullable, the return value may still be null.
		 */
		private static String nullSafeGet(final Properties properties, final String key) {
			return (properties == null) ? null : properties.getProperty(key);
		}
	}



	// hardcoded in the JAR, used as backup
	private static final Properties DEFAULTS;
	static {
		final String DEFAULT_CONFIG = "default.properties";
		final InputStream configStream = HWtoHTML.class.getResourceAsStream(DEFAULT_CONFIG);
		DEFAULTS = PropertiesUtils.loadProperties(configStream, null);
	}

	// placed in same directory alongside JAR, user configurable
	private static final String CONFIG_FILE = "config.properties";
	private static final Properties CONFIG =
			PropertiesUtils.loadProperties(CONFIG_FILE, DEFAULTS);


	private static String BASE_CSS =
			readFile(PropertiesUtils.nullSafeGet(CONFIG, "base-css"));

	/**
	 * Reads all lines from the specified file, returns the content of the file as a
	 * single string, preserving line breaks as newlines.
	 * @param path  the path to the file to be read
	 * @return  a string containing all lines of the specified file, null if file failed
	 * to read
	 */
	private static String readFile(final String path) {
		if (path == null) return null;

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
			e.printStackTrace();
			return null;
		}
	}


	public static void main(String... args) {

		if (args.length > 0) {
			if (args[0].equals("--print-css")) {
				System.out.println(BASE_CSS);
				return;
			}
			else if (args[0].equals("--print-config")) {
				if (CONFIG != null) CONFIG.list(System.out);
				return;
			}
		}
		if (args.length < 2 || (args[0].equals("--clean") && args.length < 3)) {
			System.out.println("Version: " + VERSION);
			System.out.println("Usage:");
			System.out.println("HWtoHTML <input> <output> [css]");
			System.out.println("HWtoHTML --clean <input> <output> [css]");
			System.out.println("HWtoHTML --print-config");
			System.out.println("HWtoHTML --print-css");
			return;
		}


		// css
		final boolean noDefaultCSS = args[0].equals("--clean");
		final String baseCSS = noDefaultCSS ? null : BASE_CSS;

		// engine, used to format math stuff
		final String engine = PropertiesUtils.nullSafeGet(CONFIG, "engine");


		// setup variables from args array
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
		if (cssFile == null)    // no custom CSS
			htmlDoc = newHTMLDocForHW(baseCSS, engine);
		else {  // custom CSS
			try {
				final String appendedCSS = baseCSS + '\n' + String.join("\n", Files.readAllLines(Paths.get(cssFile)));
				htmlDoc = newHTMLDocForHW(appendedCSS, engine);
			}
			catch (IOException e) {
				System.err.printf("Failed to read file '%s'%n", cssFile);
				htmlDoc = newHTMLDocForHW(baseCSS, engine);
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
	 * Creates a new HtmlDocument specifically for HWDocuments. The returned HtmlDocument
	 * contains a head element with the specified css in a style element. The engine is
	 * used as the value to the src attribute of a script element in the head element.
	 * @param css   the css source code to be included in the resulting html document
	 * @param enginePath    the location of the engine
	 */
	private static HtmlDocument newHTMLDocForHW(final String css, final String enginePath) {
		final HtmlDocument doc = new HtmlDocument();

		Element head = null;    // lazy init


		// setup engine
		if (enginePath != null && !enginePath.isEmpty()) {
			final Element engine =
					new Element("script", new Attribute("src", enginePath));

			// lazy init
			if (head == null)
				head = new Element("head");
			head.appendContent(engine);
		}

		// setup CSS
		if (css != null && !css.isEmpty()) {
			final Element style = new Element("style");
			style.appendContent(css);

			// lazy init
			if (head == null)
				head = new Element("head");
			head.appendContent(style);
		}


		if (head != null)
			doc.appendContent(head);

		return doc;
	}
}
