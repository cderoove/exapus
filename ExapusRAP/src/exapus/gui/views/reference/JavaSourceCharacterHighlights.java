package exapus.gui.views.reference;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import de.java2html.javasource.JavaSource;

public class JavaSourceCharacterHighlights {
	
	public int offset = 0;
	
	public Map<Integer, JavaSourceCharacterHighlight> highLightsPerOffset;

	public JavaSourceCharacterHighlights() {
		highLightsPerOffset =  new HashMap<Integer, JavaSourceCharacterHighlight>();
	}
		
	public void addHighlight(JavaSourceCharacterHighlight h) {
		highLightsPerOffset.put(h.range.getOffset(), h);
	}
	
	public void removeHighlight(JavaSourceCharacterHighlight h) {
		highLightsPerOffset.remove(h.range.getOffset());
	}
	
	public JavaSourceCharacterHighlight getHighLightStartingAtOffset(int o) {
//		System.out.println(offset + o);
		return highLightsPerOffset.get(o);
	}

	

}