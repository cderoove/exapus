package exapus.model.metrics;

import exapus.model.forest.*;
import exapus.model.view.View;

public class NumberOfSubAPITypesVisitor extends MetricVisitor {

    public NumberOfSubAPITypesVisitor(View view) {
        super(view);
    }

    private static void initMetric(ForestElement fe) {
        if (fe.getMetric(MetricType.API_SUB) == null) {
            fe.setMetric(new NumberOfSubAPITypes());
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
            Pattern pattern = outboundRef.getReferencingPattern();

            switch (pattern) {
                case EXTENDS_CLASS:
                case IMPLEMENTS_INTERFACE:
                case EXTENDS_INTERFACE:
                    ((NumberOfSubAPITypes) outboundRef.getMetric(MetricType.API_SUB)).addName(outboundRef.getReferencingName().toString(), outboundRef, true);
                default:
                    break;
            }
        }

        return view.isProjectCentric();
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
                    ((NumberOfSubAPITypes) inboundRef.getMetric(MetricType.API_SUB)).addName(inboundRef.getReferencingName().toString(), inboundRef, true);
                default:
                    break;
            }
        }
        return view.isAPICentric();
    }
}