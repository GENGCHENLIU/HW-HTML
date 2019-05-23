package hw;

import html.Attribute;
import html.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a table in the document.
 *
 * <p>
 * The text representation is in a format similar to markdown tables. Each row must start
 * and end with '|', and each cell must be separated using '|'.
 *
 * <p>
 * The table may have an optional header row. The header row and subsequent data rows are
 * separated by a separation row. The separation row must have at least 3 '*'
 * characters in each cell. Only the immediate previous row of this separation row is
 * formatted as a header row. If the separation row is the first row of the table, no row
 * is considered to be the header row.
 *
 * <p>
 * It is typical for a table to have at most one header row and one corresponding
 * separation row. However, more than one header row is allowed through multiple
 * separation rows.
 *
 * <p>
 * Cells are by default left aligned. The cells in the separation row may have a colon
 * ':' on the right end of the asterisks for right alignment of the corresponding column,
 * or two colons on each ends of the asterisks for center alignment. The alignment is
 * applied to the corresponding header row and all subsequent data rows until reaching
 * another header row or the end of the table.
 *
 * <p>
 * A table is not required to have an equal number of cells in each row. If a separation
 * row has more cells than the corresponding header row or subsequent data rows, the
 * alignment specified in the separation row is applied to as many cells as possible, and
 * the extra cells in the separation row is discarded. If a separation row has less cells
 * than the corresponding header row or subsequent data rows, all of the formatting will
 * be applied, and the extra cells in the header row or data rows will use the default
 * left alignment.
 */
public final class Table implements Content<String> {

	/** Alignment of the cell content. */
	private enum Align {

		LEFT(new Attribute("text-align", "left")),
		CENTERED(new Attribute("text-align", "center")),
		RIGHT(new Attribute("text-align", "right"));

		private final Attribute attribute;

		Align(final Attribute attribute) { this.attribute = attribute; }
		private Attribute toAttribute() { return attribute; }

		private static Align parse(final String s) {
			if (s.startsWith(":") && s.endsWith(":"))
				return Align.CENTERED;
			else if (s.endsWith(":"))
				return Align.RIGHT;
			else
				return Align.LEFT;
		}
	}


	private static class Cell {
		private final String content;
		private boolean isHeader;
		private Align align;

		private Cell(final String content,
		             final boolean isHeader,
		             final Align align) {
			this.content = content;
			this.isHeader = isHeader;
			this.align = align;
		}

		private Cell(final String content) { this.content = content; }
	}


	private final List<List<Cell>> rows = new ArrayList<>();
	private List<Align> lastStyles = Collections.emptyList();

	/**
	 * Constructs a table with the specified rows.
	 * @param rows  rows in markdown-like style
	 */
	public Table(final String... rows) {
		append(rows);
	}

	/**
	 * Constructs a table with the specified rows.
	 * @param rows  rows in markdown-like style
	 */
	public Table(final Collection<String> rows) {
		rows.forEach(this::append);
	}


	/** Appends a single row to this table. */
	@Override
	public void append(final String row) {
		final var cellStrings = new ArrayList<>(List.of(row.split("\\|")));

		// remove leading empty strings
		for (final var it = cellStrings.listIterator(0); it.hasNext(); ) {
			if (it.next().isEmpty())
				it.remove();
			else
				break;
		}

		// strip spaces on all cell strings
		cellStrings.replaceAll(String::strip);

		// check for header, ensure all cells have 3 asterisks
		boolean isHeader = true;
		for (final var cell : cellStrings) {
			if (!cell.contains("***")){
				isHeader = false;
				break;
			}
		}


		if (isHeader) {
			// parse to enum
			lastStyles = cellStrings.stream()
					             .map(Align::parse)
					             .collect(Collectors.toList());
			// apply to previous row, mark as header
			if (!rows.isEmpty()) {
				final var lastRow = rows.get(rows.size()-1);
				applyStyle(lastRow, lastStyles);
				lastRow.forEach(cell -> cell.isHeader = true);
			}
		}
		else {
			// convert to cells
			final List<Cell> newRow = cellStrings.stream()
					                          .map(Cell::new)
					                          .collect(Collectors.toList());
			applyStyle(newRow, lastStyles);
			rows.add(newRow);
		}
	}

	private static void applyStyle(final List<Cell> row,
	                               final List<Align> formatting) {
		final var rowIt = row.listIterator(0);
		final var styleIt = formatting.listIterator(0);

		while (rowIt.hasNext() && styleIt.hasNext())
			rowIt.next().align = styleIt.next();
	}


	/** Appends all rows to this table in the specified order. */
	public void append(final String... rows) {
		for (final var row : rows) append(row);
	}


	@Override
	public boolean isEmpty() { return rows.isEmpty(); }

	@Override
	public html.Content toHtmlContent() {
		final Element tableElement = new Element("table");

		for (final var row : rows) {
			final var rowElement = new Element("tr");

			for (final var cell : row) {
				final var cellElement = new Element(cell.isHeader ? "th" : "td");
				cellElement.appendContent(cell.content);

				if (cell.align != null)
					cellElement.addAttribute(cell.align.toAttribute());

				rowElement.appendContent(cellElement);
			}

			tableElement.appendContent(rowElement);
		}

		return tableElement;
	}
}
