package exapus.gui.editors.forest.tree;

import exapus.model.forest.InboundRef;
import exapus.model.forest.Member;
import exapus.model.forest.OutboundRef;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.Ref;
import exapus.model.visitors.CopyingForestVisitor;

public class ForestTreeFilterVisitor extends CopyingForestVisitor {

	protected boolean copyRef(Ref ref) {
		forestCopy.getCorrespondingForestElement(true, ref);
		return true;
	}

	@Override
	public boolean visitPackageTree(PackageTree packageTree) {
		return true;
	}

	@Override
	public boolean visitPackageLayer(PackageLayer packageLayer) {
		return true;
	}

	@Override
	public boolean visitMember(Member member) {
		return true;
	}

	@Override
	public boolean visitInboundReference(InboundRef inboundRef) {
		return copyRef(inboundRef);
	}

	@Override
	public boolean visitOutboundReference(OutboundRef outboundRef) {
		return copyRef(outboundRef);
	}

}
