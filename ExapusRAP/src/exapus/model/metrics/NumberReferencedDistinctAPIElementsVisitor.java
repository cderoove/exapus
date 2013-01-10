package exapus.model.metrics;

import exapus.model.forest.*;
import exapus.model.view.View;

public class NumberReferencedDistinctAPIElementsVisitor extends MetricVisitor {

    public NumberReferencedDistinctAPIElementsVisitor(View view) {
        super(view);
    }

    private static void initMetric(ForestElement fe) {
        if (fe.getMetric(Metrics.API_ELEM.getShortName()) == null) {
            fe.setMetric(new NumberReferencedDistinctAPIElements());
        }
    }

    @Override
    public boolean visitInboundFactForest(InboundFactForest forest) {
        return view.isAPICentric();
    }

    @Override
    public boolean visitOutboundFactForest(OutboundFactForest forest) {
        return view.isProjectCentric();
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
        if (view.isProjectCentric()) {
            initMetric(outboundRef);
            ((NumberReferencedDistinctAPIElements) outboundRef.getMetric(Metrics.API_ELEM.getShortName())).addName(outboundRef.getReferencedName().toString(), outboundRef, true);
        }
        return view.isProjectCentric();
    }

    @Override
    public boolean visitInboundReference(InboundRef inboundRef) {
        if (view.isAPICentric()) {
            initMetric(inboundRef);
            ((NumberReferencedDistinctAPIElements) inboundRef.getMetric(Metrics.API_ELEM.getShortName())).addName(inboundRef.getReferencedName().toString(), inboundRef, true);
        }
        return view.isAPICentric();
    }
}
