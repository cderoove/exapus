package exapus.model.view.graphbuilder;

import exapus.gui.editors.forest.graph.Edge;
import exapus.model.forest.FactForest;
import exapus.model.forest.InboundFactForest;
import exapus.model.forest.InboundRef;
import exapus.model.forest.Member;
import exapus.model.forest.OutboundFactForest;
import exapus.model.forest.OutboundRef;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.view.View;
import exapus.model.visitors.IForestVisitor;

public class ContainmentGraphBuilder extends GraphBuilder {
	
	ContainmentGraphBuilder(View v) {
		super();
	}
	
	protected IForestVisitor newVisitor() {
		 return new IForestVisitor() {
			@Override
			public boolean visitInboundFactForest(InboundFactForest forest) {
				return true;
			}

			@Override
			public boolean visitOutboundFactForest(OutboundFactForest forest) {
				return true;
			}

			@Override
			public boolean visitPackageTree(PackageTree packageTree) {
				graph.add(packageTree);
				return true;
			}

			@Override
			public boolean visitPackageLayer(PackageLayer packageLayer) {
				graph.add(packageLayer);
				graph.add(new Edge(packageLayer, packageLayer.getParent()));
				return true;
			}

			@Override
			public boolean visitMember(Member member) {
				if(member.isTopLevel()) {
					graph.add(member);
					graph.add(new Edge(member, member.getParent()));
				}
				return false;
			}

			@Override
			public boolean visitInboundReference(InboundRef inboundRef) {
				return false;
			}

			@Override
			public boolean visitOutboundReference(OutboundRef outboundRef) {
				return false;
			}
			
		 };
		
	}

}
