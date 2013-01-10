package exapus.model.metrics;

import exapus.model.forest.*;
import exapus.model.view.View;

public class APIChildrenVisitor extends MetricVisitor {

    public APIChildrenVisitor(View view) {
        super(view);
    }

    private static void initMetric(ForestElement fe) {
        if (fe.getMetric(Metrics.API_CHILDREN.getShortName()) == null) {
            fe.setMetric(new APIChildren());
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
            Pattern pattern = inboundRef.getReferencingPattern();

            switch (pattern) {
                case EXTENDS_CLASS:
                case IMPLEMENTS_INTERFACE:
                case EXTENDS_INTERFACE:
                    ((APIChildren) inboundRef.getMetric(Metrics.API_CHILDREN.getShortName())).pp(inboundRef, true);
                default:
                    break;
            }
        }
        return view.isAPICentric();
    }

    @Override
    public boolean visitOutboundReference(OutboundRef outboundRef) {
        if (view.isProjectCentric()) {
            initMetric(outboundRef);
            Pattern pattern = outboundRef.getReferencingPattern();

            switch (pattern) {
                case EXTENDS_CLASS:
                case IMPLEMENTS_INTERFACE:
                case EXTENDS_INTERFACE:
                    ((APIChildren) outboundRef.getMetric(Metrics.API_CHILDREN.getShortName())).pp(outboundRef, true);
                default:
                    break;
            }
        }

        return view.isProjectCentric();
    }
}
