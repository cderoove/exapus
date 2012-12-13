package exapus.gui.views.forest.reference;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import de.java2html.javasource.JavaSource;

public class JavaSourceLineHighlights {

	public Map<Integer, JavaSourceLineHighlight> highLightsPerLineNumber;
	
	public int lineNumberOffset = 0;
	
	public JavaSourceLineHighlights() {
		highLightsPerLineNumber = new HashMap<Integer, JavaSourceLineHighlight>();
	}
		
	public void addHighlight(JavaSourceLineHighlight h) {
		highLightsPerLineNumber.put(h.lineNumber, h);
	}
	
	public void removeHighlight(JavaSourceLineHighlight h) {
		highLightsPerLineNumber.remove(h.lineNumber);
	}
	
	public JavaSourceLineHighlight getHighlightForLine(int o) {
		return highLightsPerLineNumber.get(o);
	}

	

}