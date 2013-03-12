package exapus.model.view.graphbuilder;

import com.google.common.collect.Multiset;
import exapus.gui.editors.forest.graph.Edge;
import exapus.gui.editors.forest.graph.Node;
import exapus.model.forest.*;
import exapus.model.tags.Tag;
import exapus.model.view.View;
import exapus.model.visitors.IForestVisitor;

import java.util.HashMap;
import java.util.Map;

public class TopLevelTagsGraphBuilder extends GraphBuilder {

    private Map<ForestElement, Node> hack = new HashMap<ForestElement, Node>();

    TopLevelTagsGraphBuilder(View v) {
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
                graph.add(toNode(packageTree));
                return true;
            }

            @Override
            public boolean visitPackageLayer(PackageLayer packageLayer) {
                // We don't show branches w/o super tags
                if (!hasSuperTagsBelow(packageLayer.getAllTags())) return false;

                // We don't show branches w/o usage (when the appropriate flag is on)
                boolean noUsage = packageLayer.getMetric(getView().getMetricType()).getValue(false) == 0;
                if (getView().isGraphDetailsOnlyWithUsage() && noUsage) return false;

                Node to = toNode(packageLayer);

                Multiset<Tag> currentSuperTags = packageLayer.getTags().toMultiset(true);
                boolean isSuperTag = !currentSuperTags.isEmpty();
                if (isSuperTag && !packageLayer.hasMembers()) {
                    to.setSpCase(Node.SpecialCase.TOP_LEVEL_TAG_WO_CHILDREN);
                }
                graph.add(to);
                graph.add(new Edge(toNode(packageLayer.getParent()), to));

                // We go deeper if the current layer does not contain a super tag
                // or if a new superTag appears on the next level
                boolean anotherSuperTagBelow = false;
                if (isSuperTag) {
                    for (Tag tag : packageLayer.getAllTags().elementSet()) {
                        if (tag.isSuperTag() && !currentSuperTags.contains(tag)) {
                            anotherSuperTagBelow = true;
                            break;
                        }
                    }
                }

                return !isSuperTag || anotherSuperTagBelow;
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

    private Node toNode(ForestElement fe) {
        if (hack.containsKey(fe)) {
            return hack.get(fe);
        } else {
            Node n = new Node(fe);
            hack.put(fe, n);
            return n;
        }
    }

    private static boolean hasSuperTagsBelow(Multiset<Tag> allTags) {
        boolean res = false;

        for (Tag tag : allTags.elementSet()) {
            if (tag.isSuperTag()) {
                res = true;
                break;
            }
        }

        return res;
    }

}
