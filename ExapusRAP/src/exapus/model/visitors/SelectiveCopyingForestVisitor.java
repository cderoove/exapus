package exapus.model.visitors;

import exapus.model.forest.FactForest;
import exapus.model.forest.InboundFactForest;
import exapus.model.forest.InboundRef;
import exapus.model.forest.Member;
import exapus.model.forest.OutboundFactForest;
import exapus.model.forest.OutboundRef;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;

public abstract class SelectiveCopyingForestVisitor extends CopyingForestVisitor {


	@Override
	public boolean visitInboundFactForest(InboundFactForest forest) {
		if(select(forest)) 
			return super.visitInboundFactForest(forest);
		return false;
	}

	abstract protected boolean select(InboundFactForest forest);


	@Override
	public boolean visitOutboundFactForest(OutboundFactForest forest) {
		if(select(forest)) 
			return super.visitOutboundFactForest(forest);
		return false;	
	}
	
	abstract protected boolean select(OutboundFactForest forest);

	@Override
	public boolean visitPackageTree(PackageTree packageTree) {
		if(select(packageTree)) 
			return super.visitPackageTree(packageTree);
		return false;	
	}


	abstract protected boolean select(PackageTree packageTree);


	@Override
	public boolean visitPackageLayer(PackageLayer packageLayer) {
		if(select(packageLayer)) 
			return super.visitPackageLayer(packageLayer);
		return false;	
	}

	abstract protected boolean select(PackageLayer packageLayer);


	@Override
	public boolean visitMember(Member member) {
		if(select(member)) 
			return super.visitMember(member);
		return false;	
	}

	abstract protected boolean select(Member member);


	@Override
	public boolean visitInboundReference(InboundRef inboundRef) {
		if(select(inboundRef)) 
			return super.visitInboundReference(inboundRef);
		return false;	
	}

	abstract protected boolean select(InboundRef inboundRef);

	@Override
	public boolean visitOutboundReference(OutboundRef outboundRef) {
		if(select(outboundRef)) 
			return super.visitOutboundReference(outboundRef);
		return false;	
	}

	abstract protected boolean select(OutboundRef outboundRef);

}
