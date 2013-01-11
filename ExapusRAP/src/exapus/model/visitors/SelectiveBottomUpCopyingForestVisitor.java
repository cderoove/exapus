package exapus.model.visitors;

import exapus.model.forest.InboundFactForest;
import exapus.model.forest.InboundRef;
import exapus.model.forest.Member;
import exapus.model.forest.OutboundFactForest;
import exapus.model.forest.OutboundRef;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;

public abstract class SelectiveBottomUpCopyingForestVisitor extends CopyingForestVisitor implements ICopyingForestVisitor {
	

		@Override
		public boolean visitInboundFactForest(InboundFactForest forest) {
			super.visitInboundFactForest(forest);
			return select(forest);
		}

		abstract protected boolean select(InboundFactForest forest);


		@Override
		public boolean visitOutboundFactForest(OutboundFactForest forest) {
			super.visitOutboundFactForest(forest);
			return select(forest); 
		}
		
		abstract protected boolean select(OutboundFactForest forest);

		@Override
		public boolean visitPackageTree(PackageTree packageTree) {
			return select(packageTree);
		}

		abstract protected boolean select(PackageTree packageTree);


		@Override
		public boolean visitPackageLayer(PackageLayer packageLayer) {
			return select(packageLayer); 
		}

		abstract protected boolean select(PackageLayer packageLayer);


		@Override
		public boolean visitMember(Member member) {
			return select(member);
		}

		abstract protected boolean select(Member member);


		@Override
		public boolean visitInboundReference(InboundRef inboundRef) {
			return select(inboundRef); 
		}

		abstract protected boolean select(InboundRef inboundRef);

		@Override
		public boolean visitOutboundReference(OutboundRef outboundRef) {
			return select(outboundRef);
		}

		abstract protected boolean select(OutboundRef outboundRef);

}

