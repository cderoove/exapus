package exapus.gui.views.reference;

import java.util.Random;

import org.eclipse.jdt.core.SourceRange;

import de.java2html.options.JavaSourceStyleEntry;
import de.java2html.util.RGB;

public class JavaSourceCharacterHighlight extends JavaSourceHighlight {
		
	public SourceRange range;

	public RGB color;
	
	JavaSourceCharacterHighlight(SourceRange r, RGB c) {
		range = r;
		color = c;
	}
	
	public boolean inRange(int offset) {
		int start = range.getOffset();
		return offset >= start && offset <= (range.getLength() + start);				
	}
	
	
}
