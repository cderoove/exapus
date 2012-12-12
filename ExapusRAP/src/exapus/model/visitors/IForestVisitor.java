package exapus.model.visitors;

import exapus.model.forest.InboundFactForest;
import exapus.model.forest.InboundRef;
import exapus.model.forest.Member;
import exapus.model.forest.OutboundFactForest;
import exapus.model.forest.OutboundRef;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;

public interface IForestVisitor {
	
	//boolean visitModel(ExapusModel m);

	boolean visitInboundFactForest(InboundFactForest forest);
	
	boolean visitOutboundFactForest(OutboundFactForest forest);

	boolean visitPackageTree(PackageTree packageTree);

	boolean visitPackageLayer(PackageLayer packageLayer);

	boolean visitMember(Member member);

	boolean visitInboundReference(InboundRef inboundRef);

	boolean visitOutboundReference(OutboundRef outboundRef);


}
