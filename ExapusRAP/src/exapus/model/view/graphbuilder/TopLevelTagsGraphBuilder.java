package exapus.model.view.graphbuilder;

import com.google.common.collect.Multiset;
import exapus.gui.editors.forest.graph.Edge;
import exapus.gui.editors.forest.graph.Node;
import exapus.model.forest.*;
import exapus.model.tags.Tag;
import exapus.model.view.View;
import exapus.model.visitors.IForestVisitor;

import java.util.*;

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
                //System.err.println("Package layer = " + packageLayer.getQName().toString());

                // We don't show branches w/o super tags
                if (!hasSuperTagsBelow(packageLayer.getAllTags())) return false;

                // We don't show branches w/o usage (when the appropriate flag is on)
                boolean noUsage = packageLayer.getMetric(getView().getMetricType()).getValue(false) == 0;
                if (getView().isGraphDetailsOnlyWithUsage() && noUsage) return false;

                Node to = toNode(packageLayer);

                Multiset<Tag> currentSuperTags = packageLayer.getTags().toMultiset(true);
                boolean isSuperTag = !currentSuperTags.isEmpty();
                if (isSuperTag) {
                    if (currentSuperTags.elementSet().size() > 1) {
                        System.err.printf("Oh-oh, there is more than one super tag on %s: %s\n", packageLayer.getQName().toString(), currentSuperTags.elementSet().toString());
                    }

                    boolean isTagByPrefix = false;
                    Tag currentSuperTag = new ArrayList<Tag>(currentSuperTags.elementSet()).get(0);
                    //System.err.println("currentTag = " + currentSuperTag.toDebugString());
                    for (PackageLayer subPackage : packageLayer.getPackageLayers()) {
                        //System.err.println("\tsubPackage = " + subPackage.getQName().toString());
                        //System.err.println("\ttags = " + subPackage.getTags().toMultiset(true).toString());
                        for (Tag tag : subPackage.getTags().toMultiset(true)) {
                            if (currentSuperTag.equals(tag)) {
                                isTagByPrefix = true;
                                break;
                            }
                        }

                        if (isTagByPrefix) break;
                    }

                    //System.err.println("isTagByPrefix = " + isTagByPrefix);
                    if (isTagByPrefix) {
                        to.setSpCase(Node.SpecialCase.TOP_LEVEL_TAG_WITH_PREFIX);
                    }
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
