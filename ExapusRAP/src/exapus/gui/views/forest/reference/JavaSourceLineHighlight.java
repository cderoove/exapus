package exapus.gui.views.forest.reference;

import java.util.Random;

import org.eclipse.jdt.core.SourceRange;

import de.java2html.options.JavaSourceStyleEntry;
import de.java2html.util.RGB;

public class JavaSourceLineHighlight extends JavaSourceHighlight {
		
	public RGB color;
	
	public int lineNumber;
	
	JavaSourceLineHighlight(int l, RGB c) {
		lineNumber = l;
		color = c;
	}
	
	
}
