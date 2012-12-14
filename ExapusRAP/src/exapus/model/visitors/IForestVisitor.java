package exapus.model.visitors;

import exapus.model.forest.FactForest;
import exapus.model.forest.InboundFactForest;
import exapus.model.forest.InboundRef;
import exapus.model.forest.Member;
import exapus.model.forest.OutboundFactForest;
import exapus.model.forest.OutboundRef;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;

public interface IForestVisitor {
	
	public boolean visitInboundFactForest(InboundFactForest forest);
	
	public boolean visitOutboundFactForest(OutboundFactForest forest);

	public boolean visitPackageTree(PackageTree packageTree);

	public boolean visitPackageLayer(PackageLayer packageLayer);

	public boolean visitMember(Member member);

	public boolean visitInboundReference(InboundRef inboundRef);

	public boolean visitOutboundReference(OutboundRef outboundRef);

}
