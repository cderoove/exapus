package exapus.model.metrics;

import exapus.model.forest.*;
import exapus.model.visitors.IForestVisitor;

public class APIChildrenVisitor implements IForestVisitor {

    private static void initMetric(ForestElement fe) {
        if (fe.getMetric() == null) {
            fe.setMetric(new APIChildren());
        }
    }

    @Override
    public boolean visitInboundFactForest(InboundFactForest forest) {
        return false;
    }

    @Override
    public boolean visitOutboundFactForest(OutboundFactForest forest) {
        return true;
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
        return false;
    }

    @Override
    public boolean visitOutboundReference(OutboundRef outboundRef) {
        initMetric(outboundRef);
        Pattern pattern = outboundRef.getReferencingPattern();

        switch (pattern) {
            case EXTENDS_CLASS:
            case IMPLEMENTS_INTERFACE:
            case EXTENDS_INTERFACE:
                if (outboundRef.getMetric() instanceof APIChildren) {
                    ((APIChildren) outboundRef.getMetric()).pp(outboundRef);
                }
		default:
			break;
        }

        return true;
    }
}
