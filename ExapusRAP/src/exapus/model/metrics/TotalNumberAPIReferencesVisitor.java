package exapus.model.metrics;

import exapus.model.forest.*;
import exapus.model.visitors.IForestVisitor;

public class TotalNumberAPIReferencesVisitor implements IForestVisitor {

    private static void initMetric(ForestElement fe) {
        if (fe.getMetric(Metrics.API_REFS.getShortName()) == null) {
            fe.setMetric(new TotalNumberAPIReferences());
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
        ((TotalNumberAPIReferences) outboundRef.getMetric(Metrics.API_REFS.getShortName())).pp(outboundRef, true);
        return true;
    }
}
