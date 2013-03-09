package exapus.model.metrics;

import exapus.model.forest.*;
import exapus.model.view.View;

public class NumberOfProjsVisitor extends MetricVisitor {

    public NumberOfProjsVisitor(View view) {
        super(view);
    }

    private static void initMetric(ForestElement fe) {
        if (fe.getMetric(MetricType.PROJS) == null) {
            fe.setMetric(new NumberOfProjs());
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
            ((NumberOfProjs) outboundRef.getMetric(MetricType.PROJS)).addName(outboundRef.getDual().getParentPackageTree().getName().toString(), outboundRef, true);
        }
        return view.isProjectCentric();
    }

    @Override
    public boolean visitInboundReference(InboundRef inboundRef) {
        if (view.isAPICentric()) {
            initMetric(inboundRef);
            ((NumberOfProjs) inboundRef.getMetric(MetricType.PROJS)).addName(inboundRef.getDual().getParentPackageTree().getName().toString(), inboundRef, true);
        }
        return view.isAPICentric();
    }
}
