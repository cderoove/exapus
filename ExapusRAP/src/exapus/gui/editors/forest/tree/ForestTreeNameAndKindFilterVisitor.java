package exapus.gui.editors.forest.tree;

import exapus.model.forest.Element;
import exapus.model.forest.Pattern;
import exapus.model.forest.Ref;


public class ForestTreeNameAndKindFilterVisitor extends ForestTreeNameFilterVisitor {

	private Pattern pattern = Pattern.ANY;
	
	private Element element = Element.ANY;
	
	@Override
	protected boolean copyRef(Ref ref) {
		if(pattern != Pattern.ANY) {
			if(pattern != ref.getPattern())
				return false;
		}
		if(element != Element.ANY) {
			if(element != ref.getElement())
				return false;
		}
		return super.copyRef(ref);
	}
	
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public void setElement(Element element) {
		this.element = element;
	}


}
