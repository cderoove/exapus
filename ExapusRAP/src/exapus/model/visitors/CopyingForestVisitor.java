package exapus.model.visitors;

import exapus.model.forest.ExapusModel;
import exapus.model.forest.FactForest;
import exapus.model.forest.InboundFactForest;
import exapus.model.forest.OutboundFactForest;

public abstract class CopyingForestVisitor implements ICopyingForestVisitor {

	public CopyingForestVisitor() {
		clear();
	}
	
	@Override
	public FactForest copy(FactForest f) {
		clear();
		f.acceptVisitor(this);
		return getCopy();
	}
	
	protected FactForest forestCopy;

	protected void clear() {
		forestCopy = null;
	}
	
	protected FactForest getCopy() {
		return forestCopy;
	}
	
	@Override
	public boolean visitInboundFactForest(InboundFactForest forest) {
		ExapusModel dummyModel = new ExapusModel();
		forestCopy = new InboundFactForest(dummyModel);
		return true;
	}

	@Override
	public boolean visitOutboundFactForest(OutboundFactForest forest) {
		ExapusModel dummyModel = new ExapusModel();
		forestCopy = new OutboundFactForest(dummyModel);
		return true;
	}


	

}
