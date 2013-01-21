package exapus.model.view.graphbuilder;

import exapus.gui.editors.forest.graph.Edge;
import exapus.model.forest.*;
import exapus.model.view.View;
import exapus.model.visitors.IForestVisitor;

public class GroupedPackagesGraphBuilder extends GraphBuilder {

    GroupedPackagesGraphBuilder(View v) {
        super(v);
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
                graph.add(new Edge(packageLayer.getParent(), packageLayer));
                return true;
            }

            @Override
            public boolean visitMember(Member member) {
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
