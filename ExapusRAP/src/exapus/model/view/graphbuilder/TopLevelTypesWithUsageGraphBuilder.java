package exapus.model.view.graphbuilder;

import exapus.gui.editors.forest.graph.Edge;
import exapus.model.forest.*;
import exapus.model.view.View;
import exapus.model.visitors.IForestVisitor;

public class TopLevelTypesWithUsageGraphBuilder extends GraphBuilder {

    TopLevelTypesWithUsageGraphBuilder(View v) {
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
                if (member.isTopLevel()) {
                    if (member.getMetric(getView().getMetricType()).getValue(true) > 0) {
                        graph.add(member);
                        graph.add(new Edge(member.getParent(), member));
                    }
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
