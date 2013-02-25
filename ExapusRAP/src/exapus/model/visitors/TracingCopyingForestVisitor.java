package exapus.model.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import exapus.model.forest.ForestElement;


public abstract class TracingCopyingForestVisitor extends CopyingForestVisitor {
	
	protected void clear() {
		super.clear();
		copies = new HashMap<ForestElement,ForestElement>();
	}

	private Map<ForestElement,ForestElement> copies;

	
	protected void registerCopy(ForestElement original, ForestElement copy) {
		copies.put(original, copy);
	}

	protected ForestElement getCopy(ForestElement original) {
		return copies.get(original);
	}

	protected boolean hasCopy(ForestElement original) {
		return copies.containsKey(original);
	}
		
	public Object[] getCopies(Object[] originals) {
		ArrayList<Object> copied = new ArrayList<Object>(originals.length);
		for(Object original : originals) {
			if(original instanceof ForestElement) {
				ForestElement copy = getCopy((ForestElement) original);
				if(copy != null)
					copied.add(copy);
			}
		}
		return copied.toArray();
	}
		

	

}
