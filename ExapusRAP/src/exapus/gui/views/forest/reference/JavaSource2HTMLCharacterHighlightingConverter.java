package exapus.gui.views.forest.reference;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Set;

import javax.swing.text.Highlighter.Highlight;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;

import de.java2html.Version;
import de.java2html.converter.AbstractJavaSourceConverter;
import de.java2html.converter.ConverterMetaData;
import de.java2html.converter.JavaSource2HTMLConverter;
import de.java2html.javasource.JavaSource;
import de.java2html.javasource.JavaSourceIterator;
import de.java2html.javasource.JavaSourceRun;
import de.java2html.javasource.JavaSourceType;
import de.java2html.options.HorizontalAlignment;
import de.java2html.options.IHorizontalAlignmentVisitor;
import de.java2html.options.JavaSourceConversionOptions;
import de.java2html.options.JavaSourceStyleEntry;
import de.java2html.options.JavaSourceStyleTable;
import de.java2html.util.HtmlUtilities;
import de.java2html.util.RGB;
import de.java2html.util.StringHolder;


public class JavaSource2HTMLCharacterHighlightingConverter extends AbstractJavaSourceConverter {
	//would have liked to specialize  JavaSource2HTMLConverter, but methods to be overridden are private so code is cloned from there

	private final static String HTML_SITE_HEADER = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
			//     "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"
			// \"http://www.w3.org/TR/html4/loose.dtd\">\n"
			+ "<html><head>\n"
			+ "<title>{0}</title>\n"
			+ "  <style type=\"text/css\">\n"
			+ "    <!--code '{' font-family: Courier New, Courier; font-size: 10pt; margin: 0px; '}'-->\n"
			+ "  </style>\n"
			+ "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />\n"
			+ "</head><body>\n";

	private final static String HTML_SITE_FOOTER = "\n</body></html>";

	private final static String HTML_BLOCK_SEPARATOR = "<p />\n";

	private final static String HTML_BLOCK_HEADER = "\n\n"
			+ "<div align=\"{0}\" class=\"java\">\n"
			+ "<table border=\"{1}\" cellpadding=\"3\" "
			+ "cellspacing=\"0\" bgcolor=\"{2}\">\n";

	private final static String HTML_HEAD_START = "  <!-- start headline -->\n"
			+ "   <tr>\n"
			+ "    <td colspan=\"2\">\n"
			+ "     <center><font size=\"+2\">\n"
			+ "      <code><b>\n";

	private final static String HTML_HEAD_END = "      </b></code>\n"
			+ "     </font></center>\n"
			+ "    </td>\n"
			+ "   </tr>\n"
			+ "  <!-- end headline -->\n";

	private final static String HTML_COL2_START = "  <!-- start source code -->\n"
			+ "   <td nowrap=\"nowrap\" valign=\"top\" align=\"left\">\n"
			+ "    <code>\n";

	private final static String HTML_COL2_END = "</code>\n"
			+ "    \n"
			+ "   </td>\n"
			+ "  <!-- end source code -->\n";


	private final static String HTML_BLOCK_FOOTER =
			"</table>\n"
					+ "</div>\n";


	private int lineCifferCount;
	
	private int currentIndex = 0;
	private int backgroundStartIndex = 0;		
	private int backgroundEndIndex = 0;

	private JavaSourceCharacterHighlights sourceHighlights;


	public JavaSource2HTMLCharacterHighlightingConverter() {
		super(new ConverterMetaData("html", "XHTML 1.0 Transitional (inlined fonts)", "html"));
	}

	public String getDocumentHeader(JavaSourceConversionOptions options, String title) {
		if (title == null) {
			title = ""; //$NON-NLS-1$
		}
		return MessageFormat.format(HTML_SITE_HEADER, new Object[]{ title });
	}


	public void convert(JavaSource source, JavaSourceConversionOptions options, BufferedWriter writer)
			throws IOException {
		if (source == null) {
			throw new IllegalStateException("Trying to write out converted code without having source set.");
		}

		//Header
		String alignValue = getHtmlAlignValue(options.getHorizontalAlignment());
		String bgcolorValue = options.getStyleTable().get(JavaSourceType.BACKGROUND).getHtmlColor();
		String borderValue = options.isShowTableBorder() ? "2" : "0";

		writer.write(MessageFormat.format(HTML_BLOCK_HEADER, new Object[]{ alignValue, borderValue, bgcolorValue }));

		if (options.isShowFileName() && source.getFileName() != null) {
			writeFileName(source, writer);
		}

		writer.write("   <tr>");
		writer.newLine();

		writeSourceCode(source, options, writer);

		writer.write("   </tr>");
		writer.newLine();

		writer.write(HTML_BLOCK_FOOTER);
	}

	private String getHtmlAlignValue(HorizontalAlignment alignment) {
		final StringHolder stringHolder = new StringHolder();
		alignment.accept(new IHorizontalAlignmentVisitor() {
			public void visitLeftAlignment(HorizontalAlignment horizontalAlignment) {
				stringHolder.setValue("left");
			}

			public void visitRightAlignment(HorizontalAlignment horizontalAlignment) {
				stringHolder.setValue("right");
			}

			public void visitCenterAlignment(HorizontalAlignment horizontalAlignment) {
				stringHolder.setValue("center");
			}
		});
		return stringHolder.getValue();
	}

	private void writeFileName(JavaSource source, BufferedWriter writer) throws IOException {
		writer.write(HTML_HEAD_START);
		writer.write(source.getFileName());
		writer.newLine();
		writer.write(HTML_HEAD_END);
	}

	private void writeSourceCode(JavaSource source, JavaSourceConversionOptions options, BufferedWriter writer)
			throws IOException {
		writer.write(HTML_COL2_START);


		currentIndex = 0;
		
		lineCifferCount = String.valueOf(source.getLineCount()).length();

		JavaSourceIterator iterator = source.getIterator();
		int currentLineNumber = 1;
		while (iterator.hasNext()) {
			JavaSourceRun run = iterator.getNext();
			if (run.isAtStartOfLine()) {
				if (options.isAddLineAnchors()) {
					writeLineAnchorStart(options, writer, currentLineNumber);
				}
				if (options.isShowLineNumbers()) {
					writeLineNumber(options, writer, currentLineNumber);
				}
				if (options.isAddLineAnchors()) {
					writeLineAnchorEnd(writer);
				}
				currentLineNumber++;
			}

			toHTML(options.getStyleTable(), run, writer);
			if (run.isAtEndOfLine() && iterator.hasNext()) {
				writer.write("<br />");
				writer.newLine();
			}
		}
		writer.write(HTML_COL2_END);
	}

	private void writeLineAnchorEnd(BufferedWriter writer) throws IOException {
		writer.write("</a>");
	}

	private void writeLineAnchorStart(JavaSourceConversionOptions options, BufferedWriter writer, int lineNumber)
			throws IOException {
		writer.write("<a name=\"");
		writer.write(options.getLineAnchorPrefix() + lineNumber);
		writer.write("\">");
	}

	private void writeLineNumber(JavaSourceConversionOptions options, BufferedWriter writer, int lineNo)
			throws IOException {


		JavaSourceStyleEntry styleEntry = options.getStyleTable().get(JavaSourceType.LINE_NUMBERS);
		writeStyleStart(writer, styleEntry);

		String lineNumber = String.valueOf(lineNo);
		int cifferCount = lineCifferCount - lineNumber.length();
		while (cifferCount > 0) {
			writer.write('0');
			--cifferCount;
		}

		writer.write(lineNumber);
		writeStyleEnd(writer, styleEntry);
		writer.write("&nbsp;");
	}

	private void toHTML(JavaSourceStyleTable styleTable, JavaSourceRun run, BufferedWriter writer)
			throws IOException {

		JavaSourceStyleEntry style = styleTable.get(run.getType());
		//TODO: is this the cause of the off character indices?
		String t = HtmlUtilities.encode(run.getCode(), "\n ");

		for (int i = 0; i < t.length(); ++i) {
			Collection<JavaSourceCharacterHighlight> values = sourceHighlights.highLightsPerOffset.values();
			Collection<JavaSourceCharacterHighlight> applicable = Collections2.filter(values, new Predicate<JavaSourceCharacterHighlight>() {
				public boolean apply(JavaSourceCharacterHighlight h) {
					return h.inRange(currentIndex + sourceHighlights.offset);
				}
			});
			
			if(applicable.isEmpty()) 
				writeStyleStart(writer, style, null);
			 else
				 writeStyleStart(writer, style, applicable.iterator().next().color);
				
			
			
			char ch = t.charAt(i);
			if (ch == ' ') {
				writer.write("&nbsp;");
			}
			else {
				writer.write(ch);
			}
			currentIndex++;

			writeStyleEnd(writer, style);

		}




	}


	private void writeStyleStart(BufferedWriter writer, JavaSourceStyleEntry style, RGB background)  throws IOException {
		writer.write("<span style=\"" + "color:" +style.getHtmlColor() + 
				(background != null ? 
						"; background-color:" +  
						HtmlUtilities.toHTML(background)  
						: "")
				+  ";\">");
		if (style.isBold()) {
			writer.write("<b>");
		}
		if (style.isItalic()) {
			writer.write("<i>");
		}
	}

	
	private void writeStyleStart(BufferedWriter writer, JavaSourceStyleEntry style)  throws IOException {
		writer.write("<span style=\"" + "color:" + style.getHtmlColor() +  ";\">");
		if (style.isBold()) {
			writer.write("<b>");
		}
		if (style.isItalic()) {
			writer.write("<i>");
		}
	}

	private void writeStyleEnd(BufferedWriter writer, JavaSourceStyleEntry style) throws IOException {
		if (style.isItalic()) {
			writer.write("</i>");
		}
		if (style.isBold()) {
			writer.write("</b>");
		}
		writer.write("</span>");
	}

	public String getDocumentFooter(JavaSourceConversionOptions options) {
		return HTML_SITE_FOOTER;
	}

	public String getBlockSeparator(JavaSourceConversionOptions options) {
		return HTML_BLOCK_SEPARATOR;
	}

	public JavaSourceCharacterHighlights getSourceHighlights() {
		return sourceHighlights;
	}

	public void setSourceHighlights(JavaSourceCharacterHighlights sourceHighlights) {
		this.sourceHighlights = sourceHighlights;
	}


}