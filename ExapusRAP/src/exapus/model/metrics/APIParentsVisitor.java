package exapus.model.metrics;

import exapus.model.forest.*;
import exapus.model.visitors.IForestVisitor;

public class APIParentsVisitor implements IForestVisitor {

    private static void initMetric(ForestElement fe) {
        if (fe.getMetric() == null) {
            fe.setMetric(new APIParents());
        }
    }

    @Override
    public boolean visitInboundFactForest(InboundFactForest forest) {
        return true;
    }

    @Override
    public boolean visitOutboundFactForest(OutboundFactForest forest) {
        return false;
    }

    @Override
    public boolean visitPackageTree(PackageTree packageTree) {
        initMetric(packageTree);
        return true;
    }

    @Override
    public boolean visitPackageLayer(PackageLayer packageLayer) {
        initMetric(packageLayer);
        return true;
    }

    @Override
    public boolean visitMember(Member member) {
        initMetric(member);
        return true;
    }

    @Override
    public boolean visitInboundReference(InboundRef inboundRef) {
        initMetric(inboundRef);
        Pattern pattern = inboundRef.getReferencingPattern();

        switch (pattern) {
            case EXTENDS_CLASS:
            case IMPLEMENTS_INTERFACE:
            case EXTENDS_INTERFACE:
                if (inboundRef.getMetric() instanceof APIParents) {
                    ((APIParents) inboundRef.getMetric()).pp(inboundRef, true);
                }
            default:
                break;
        }

        return true;
    }

    @Override
    public boolean visitOutboundReference(OutboundRef outboundRef) {
        return false;
    }
}
