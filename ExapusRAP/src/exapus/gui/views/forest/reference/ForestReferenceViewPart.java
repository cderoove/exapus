package exapus.gui.views.forest.reference;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Random;

import org.eclipse.jdt.core.SourceRange;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.java2html.Java2Html;
import de.java2html.converter.JavaSource2HTMLConverter;
import de.java2html.javasource.JavaSource;
import de.java2html.javasource.JavaSourceParser;
import de.java2html.options.JavaSourceConversionOptions;
import de.java2html.util.RGB;
import exapus.gui.editors.SelectedForestElementBrowserViewPart;
import exapus.model.forest.Direction;
import exapus.model.forest.ForestElement;
import exapus.model.forest.Member;
import exapus.model.forest.MemberContainer;
import exapus.model.forest.OutboundRef;
import exapus.model.forest.Ref;

//TODO: could try being nothing of clicks through BrowserFunction, or clientSide script (incubator components)

public class ForestReferenceViewPart extends SelectedForestElementBrowserViewPart {

	public static final String ID = "exapus.gui.views.forest.ForestReferenceView";

	private JavaSource2HTMLLineHighlightingConverter converter;

	private static JavaSourceConversionOptions CONVERSION_OPTIONS;

	{
		CONVERSION_OPTIONS = JavaSourceConversionOptions.getDefault();
		CONVERSION_OPTIONS.setAddLineAnchors(false); //can't jump to them anyway (perhaps through JS)
		CONVERSION_OPTIONS.setShowLineNumbers(true);
	}


	protected String textToRender(ForestElement fe) {
		if(fe.getParentFactForest().getDirection().equals(Direction.INBOUND)) {
			if(fe instanceof Ref)
				fe = ((Ref) fe).getDual();
			else
				return "Cannot show source of binary file.";
		}
		String source = fe.getSourceString();
		String html = "";
		if(null != source)  {
			JavaSourceLineHighlights highlights = new JavaSourceLineHighlights();
			highlights.lineNumberOffset = fe.getSourceLineNumberOffset();
			if(fe instanceof Member) {
				Member m = (Member) fe;
				for(Ref r : m.getAllReferences()) {
					SourceRange range = r.getSourceRange();
					RGB color = JavaSourceHighlight.generateRandomPastelColor();
					highlights.addHighlight(new JavaSourceLineHighlight(r.getLineNumber(), color));
				}
			}
			if(fe instanceof Ref) {
				Ref r = (Ref) fe;
				SourceRange range = r.getSourceRange();
				RGB color = JavaSourceHighlight.generateRandomPastelColor();
				highlights.addHighlight(new JavaSourceLineHighlight(r.getLineNumber(), color));
			}
			html = snippetToHTML(source, highlights);
		}
		return html;

	}
	
	private String snippetToHTML(String snippet, JavaSourceLineHighlights highlights) {
		JavaSource source;
		try {
			source = new JavaSourceParser().parse(new StringReader(snippet));
			StringWriter writer = new StringWriter();
			converter = new JavaSource2HTMLLineHighlightingConverter();
			converter.setSourceHighlights(highlights);
			converter.convert(source, CONVERSION_OPTIONS, writer);
			return writer.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	
	



}
