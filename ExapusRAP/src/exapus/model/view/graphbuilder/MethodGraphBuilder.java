package exapus.model.view.graphbuilder;

import exapus.gui.editors.forest.graph.Edge;
import exapus.model.forest.*;
import exapus.model.view.View;
import exapus.model.visitors.IForestVisitor;

public class MethodGraphBuilder extends GraphBuilder {

    MethodGraphBuilder(View v) {
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
                if (getView().isGraphDetailsOnlyWithUsage()) {
                    if (packageLayer.getMetric(getView().getMetricType()).getValue(false) > 0) {
                        graph.add(packageLayer);
                        graph.add(new Edge(packageLayer.getParent(), packageLayer));
                    }
                } else {
                    graph.add(packageLayer);
                    graph.add(new Edge(packageLayer.getParent(), packageLayer));
                }
                return true;
            }

            @Override
            public boolean visitMember(Member member) {
                boolean isMethodOfTopLevelType = member.getElement().isMethod() && (member.getParent() instanceof Member) &&
                        ((Member) member.getParent()).isTopLevel();

                if (member.isTopLevel() || isMethodOfTopLevelType) {
                    if (getView().isGraphDetailsOnlyWithUsage()) {
                        if (member.getMetric(getView().getMetricType()).getValue(true) > 0) {
                            graph.add(member);
                            graph.add(new Edge(member.getParent(), member));
                        }
                    } else {
                        graph.add(member);
                        graph.add(new Edge(member.getParent(), member));
                    }
                }
                return true;
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
