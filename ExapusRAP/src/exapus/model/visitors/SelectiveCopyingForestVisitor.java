package exapus.model.visitors;

import exapus.model.forest.FactForest;
import exapus.model.forest.InboundFactForest;
import exapus.model.forest.InboundRef;
import exapus.model.forest.Member;
import exapus.model.forest.OutboundFactForest;
import exapus.model.forest.OutboundRef;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;

public abstract class SelectiveCopyingForestVisitor implements IForestVisitor {

	private CopyingForestVisitor copyingVisitor;

	public SelectiveCopyingForestVisitor() {
		clear();
	}

	protected void clear() {
		copyingVisitor = new CopyingForestVisitor();
	}

	public FactForest copy(FactForest f) {
		clear();
		f.acceptVisitor(this);
		return getCopy();
	}

	protected FactForest getCopy() {
		return copyingVisitor.getCopy();
	}


	@Override
	public boolean visitInboundFactForest(InboundFactForest forest) {
		if(select(forest)) {
			copyingVisitor.visitInboundFactForest(forest);
			return selectChildren(forest);
		}
		return false;
	}

	protected boolean selectChildren(InboundFactForest forest) {
		return true;
	}

	abstract protected boolean select(InboundFactForest forest);


	@Override
	public boolean visitOutboundFactForest(OutboundFactForest forest) {
		if(select(forest)) {
			copyingVisitor.visitOutboundFactForest(forest);
			return selectChildren(forest);
		}
		return false;	
	}

	protected boolean selectChildren(OutboundFactForest forest) {
		return true;
	}

	abstract protected boolean select(OutboundFactForest forest);


	@Override
	public boolean visitPackageTree(PackageTree packageTree) {
		if(select(packageTree)) {
			copyingVisitor.visitPackageTree(packageTree);
			return selectChildren(packageTree);
		}
		return false;	
	}

	protected boolean selectChildren(PackageTree packageTree) {
		return true;
	}

	abstract protected boolean select(PackageTree packageTree);


	@Override
	public boolean visitPackageLayer(PackageLayer packageLayer) {
		if(select(packageLayer)) {
			copyingVisitor.visitPackageLayer(packageLayer);
			return selectChildren(packageLayer);
		}
		return false;	
	}

	protected boolean selectChildren(PackageLayer packageLayer) {
		return true;
	}

	abstract protected boolean select(PackageLayer packageLayer);


	@Override
	public boolean visitMember(Member member) {
		if(select(member)) {
			copyingVisitor.visitMember(member);
			return selectChildren(member);
		}
		return false;	
	}

	protected boolean selectChildren(Member member) {
		return true;
	}

	abstract protected boolean select(Member member);


	@Override
	public boolean visitInboundReference(InboundRef inboundRef) {
		if(select(inboundRef)) {
			copyingVisitor.visitInboundReference(inboundRef);
			return selectChildren(inboundRef);
		}
		return false;	
	}

	protected boolean selectChildren(InboundRef inboundRef) {
		return true;
	}

	abstract protected boolean select(InboundRef inboundRef);

	@Override
	public boolean visitOutboundReference(OutboundRef outboundRef) {
		if(select(outboundRef)) {
			copyingVisitor.visitOutboundReference(outboundRef);
			return selectChildren(outboundRef);
		}
		return false;	
	}

	protected boolean selectChildren(OutboundRef outboundRef) {
		return true;
	}

	abstract protected boolean select(OutboundRef outboundRef);

}
