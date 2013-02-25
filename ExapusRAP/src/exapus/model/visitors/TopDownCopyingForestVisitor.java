package exapus.model.visitors;

import java.util.HashMap;
import java.util.Map;

import exapus.model.forest.ExapusModel;
import exapus.model.forest.FactForest;
import exapus.model.forest.ForestElement;
import exapus.model.forest.ILayerContainer;
import exapus.model.forest.InboundFactForest;
import exapus.model.forest.InboundRef;
import exapus.model.forest.Member;
import exapus.model.forest.MemberContainer;
import exapus.model.forest.OutboundFactForest;
import exapus.model.forest.OutboundRef;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;

public class TopDownCopyingForestVisitor extends TracingCopyingForestVisitor implements ICopyingForestVisitor {

	public TopDownCopyingForestVisitor() {
		super();
	}

	@Override
	public boolean visitPackageTree(PackageTree packageTree) {
		PackageTree copy = new PackageTree(packageTree.getName());
		registerCopy(packageTree,copy);
		forestCopy.addPackageTree(copy); 
		return true;
	}

	@Override
	public boolean visitPackageLayer(PackageLayer packageLayer) {
		PackageLayer copy = new PackageLayer(packageLayer.getName());
		ForestElement parentCopy = getCopy(packageLayer.getParent());
		//PackageTree or PackageLayer
		ILayerContainer parentCopyAsLayerContainer = (ILayerContainer) parentCopy;
		parentCopyAsLayerContainer.addLayer(copy); 
		//copy.setParent(parentCopy);//already performed by addLayer
		registerCopy(packageLayer,copy);
		return true;
	}

	@Override
	public boolean visitMember(Member member) {
		Member copy = new Member(member.getName(), member.getElement());
		ForestElement parentCopy = getCopy(member.getParent());
		//PackageLayer or Member
		MemberContainer parentCopyAsMemberContainer = (MemberContainer) parentCopy;
		parentCopyAsMemberContainer.addMember(copy);
		registerCopy(member,copy);
		return true;
	}


	protected void copyInboundReference(InboundRef inboundRef) {
		InboundRef copy = InboundRef.fromInboundRef(inboundRef);
		ForestElement parentCopy = getCopy(inboundRef.getParent());
		Member parentCopyAsMember = (Member) parentCopy;
		parentCopyAsMember.addAPIReference(copy);
		registerCopy(inboundRef,copy);
	}
	
	protected void copyOutboundReference(OutboundRef outboundRef) {
		OutboundRef copy = OutboundRef.fromOutboundRef(outboundRef);
		copy.setDual(outboundRef.getDual());
		ForestElement parentCopy = getCopy(outboundRef.getParent());
		Member parentCopyAsMember = (Member) parentCopy;
		parentCopyAsMember.addAPIReference(copy);
		registerCopy(outboundRef,copy);
	}
	
	@Override
	public boolean visitInboundReference(InboundRef inboundRef) {
		copyInboundReference(inboundRef);
		return true;
	}

	@Override
	public boolean visitOutboundReference(OutboundRef outboundRef) {
		copyOutboundReference(outboundRef);
		return true;
	}

}
