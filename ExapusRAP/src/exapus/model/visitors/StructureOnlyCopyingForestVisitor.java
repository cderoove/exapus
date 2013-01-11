package exapus.model.visitors;

import exapus.model.forest.ForestElement;
import exapus.model.forest.InboundRef;
import exapus.model.forest.Member;
import exapus.model.forest.OutboundRef;

public class StructureOnlyCopyingForestVisitor extends TopDownCopyingForestVisitor {

	//abolished because does not work nicely with anything that traverses the tree bottom-up (e.g., table view / metrics calculation) 
	public StructureOnlyCopyingForestVisitor() {
		super();
	}
	
	@Override
	public boolean visitInboundReference(InboundRef inboundRef) {
		ForestElement originalParent = inboundRef.getParent();
		ForestElement parentCopy = getCopy(originalParent);
		Member parentCopyAsMember = (Member) parentCopy;
		parentCopyAsMember.addAPIReference(inboundRef);  //will change parent
		inboundRef.setParent(originalParent);
		return true;
	}

	@Override
	public boolean visitOutboundReference(OutboundRef outboundRef) {
		ForestElement originalParent = outboundRef.getParent();
		ForestElement parentCopy = getCopy(originalParent);
		Member parentCopyAsMember = (Member) parentCopy;
		parentCopyAsMember.addAPIReference(outboundRef); //will change parent
		outboundRef.setParent(originalParent);
		return true;
	}

	

}
