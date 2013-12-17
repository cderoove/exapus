package exapus.model.metrics;

import exapus.model.forest.*;
import exapus.model.view.View;

public class NumberOfSuperAPITypesVisitor extends MetricVisitor {

    public NumberOfSuperAPITypesVisitor(View view) {
        super(view);
    }

    private static void initMetric(ForestElement fe) {
        if (fe.getMetric(MetricType.API_SUPER) == null) {
            fe.setMetric(new NumberOfSuperAPITypes());
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
            	case EXTENDS_ENUM:	
                case EXTENDS_CLASS:
                case IMPLEMENTS_INTERFACE:
                case EXTENDS_INTERFACE:
                    ((NumberOfSuperAPITypes) outboundRef.getMetric(MetricType.API_SUPER)).addName(outboundRef.getReferencedName().toString(), outboundRef, true);
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
            	case EXTENDS_ENUM:	
                case EXTENDS_CLASS:
                case IMPLEMENTS_INTERFACE:
                case EXTENDS_INTERFACE:
                    ((NumberOfSuperAPITypes) inboundRef.getMetric(MetricType.API_SUPER)).addName(inboundRef.getReferencedName().toString(), inboundRef, true);
                default:
                    break;
            }
        }
        return view.isAPICentric();
    }
}