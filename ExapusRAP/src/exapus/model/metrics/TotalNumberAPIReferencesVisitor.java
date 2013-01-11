package exapus.model.metrics;

import exapus.model.forest.*;
import exapus.model.view.View;

public class TotalNumberAPIReferencesVisitor extends MetricVisitor {

    public TotalNumberAPIReferencesVisitor(View view) {
        super(view);
    }

    private static void initMetric(ForestElement fe) {
        if (fe.getMetric(MetricType.API_REFS) == null) {
            fe.setMetric(new TotalNumberAPIReferences());
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
    public boolean visitInboundReference(InboundRef inboundRef) {
        if (view.isAPICentric()) {
            initMetric(inboundRef);
            ((TotalNumberAPIReferences) inboundRef.getMetric(MetricType.API_REFS)).pp(inboundRef, true);
        }
        return view.isAPICentric();
    }

    @Override
    public boolean visitOutboundReference(OutboundRef outboundRef) {
        if (view.isProjectCentric()) {
            initMetric(outboundRef);
            ((TotalNumberAPIReferences) outboundRef.getMetric(MetricType.API_REFS)).pp(outboundRef, true);
        }
        return view.isProjectCentric();
    }
}
