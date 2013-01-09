package exapus.model.metrics;

import exapus.model.forest.*;
import exapus.model.visitors.IForestVisitor;

public class NumberReferencedDistinctAPIElementsVisitor implements IForestVisitor {
    private static void initMetric(ForestElement fe) {
        if (fe.getMetric() == null) {
            fe.setMetric(new NumberReferencedDistinctAPIElements());
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
    public boolean visitOutboundReference(OutboundRef outboundRef) {
        initMetric(outboundRef);
        if (outboundRef.getMetric() instanceof NumberReferencedDistinctAPIElements) {
            ((NumberReferencedDistinctAPIElements) outboundRef.getMetric()).addName(outboundRef.getReferencedName().toString(), outboundRef, true);
        }
        return true;
    }

    @Override
    public boolean visitInboundReference(InboundRef inboundRef) {
        return false;
    }
}
