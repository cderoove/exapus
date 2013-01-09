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

public class CopyingForestVisitor implements IForestVisitor {

	public CopyingForestVisitor() {
		clear();
	}

	protected void clear() {
		forestCopy = null;
		copies = new HashMap<ForestElement, ForestElement>();
	}

	public FactForest copy(FactForest f) {
		clear();
		f.acceptVisitor(this);
		return getCopy();
	}

	private Map<ForestElement,ForestElement> copies;
	private FactForest forestCopy;

	protected FactForest getCopy() {
		return forestCopy;
	}
	
	protected void registerCopy(ForestElement original, ForestElement copy) {
		copies.put(original, copy);
	}

	protected ForestElement getCopy(ForestElement original) {
		return copies.get(original);
	}

	protected boolean hasCopy(ForestElement original) {
		return copies.containsKey(original);
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

	@Override
	public boolean visitInboundReference(InboundRef inboundRef) {
		InboundRef copy = new InboundRef(inboundRef.getReferencingPattern(), inboundRef.getReferencingElement(), inboundRef.getReferencingName(), inboundRef.getSourceRange(), inboundRef.getLineNumber());
		copy.setDual(inboundRef.getDual());
		ForestElement parentCopy = getCopy(inboundRef.getParent());
		Member parentCopyAsMember = (Member) parentCopy;
		parentCopyAsMember.addAPIReference(copy);
		registerCopy(inboundRef,copy);
		return true;
	}

	@Override
	public boolean visitOutboundReference(OutboundRef outboundRef) {
		OutboundRef copy = new OutboundRef(outboundRef.getReferencingPattern(), outboundRef.getReferencedElement(), outboundRef.getReferencedName(), outboundRef.getSourceRange(), outboundRef.getLineNumber());
		copy.setDual(outboundRef.getDual());
		ForestElement parentCopy = getCopy(outboundRef.getParent());
		Member parentCopyAsMember = (Member) parentCopy;
		parentCopyAsMember.addAPIReference(copy);
		registerCopy(outboundRef,copy);
		return true;
	}

}
