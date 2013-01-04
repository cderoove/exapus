package exapus.model.metrics;

import exapus.model.forest.*;
import exapus.model.visitors.IForestVisitor;

import java.util.HashSet;
import java.util.Set;

/**
 * Naive and very expensive: almost 100 sec for one project (ant).
 * //TODO: optimize
 */
public class NumberReferencedDistinctAPIElementsVisitor implements IForestVisitor {
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
        Set<String> apiElements = new HashSet<String>();
        for (Ref ref : packageTree.getAllReferences()) {
            apiElements.add(ref.getReferencedName().toString());
        }

        if (packageTree.getMetric() instanceof NumberReferencedDistinctAPIElements) {
            ((NumberReferencedDistinctAPIElements) packageTree.getMetric()).setValue(apiElements.size());
        }

        return true;
    }

    @Override
    public boolean visitPackageLayer(PackageLayer packageLayer) {
        Set<String> apiElements = new HashSet<String>();
        for (Ref ref : packageLayer.getAllReferences()) {
            apiElements.add(ref.getReferencedName().toString());
        }

        if (packageLayer.getMetric() instanceof NumberReferencedDistinctAPIElements) {
            ((NumberReferencedDistinctAPIElements) packageLayer.getMetric()).setValue(apiElements.size());
        }

        return true;
    }

    @Override
    public boolean visitMember(Member member) {
        Set<String> apiElements = new HashSet<String>();
        for (Ref ref : member.getAllReferences()) {
            apiElements.add(ref.getReferencedName().toString());
        }

        if (member.getMetric() instanceof NumberReferencedDistinctAPIElements) {
            ((NumberReferencedDistinctAPIElements) member.getMetric()).setValue(apiElements.size());
        }

        return true;
    }

    @Override
    public boolean visitInboundReference(InboundRef inboundRef) {
        return false;
    }

    @Override
    public boolean visitOutboundReference(OutboundRef outboundRef) {
        if (outboundRef.getMetric() instanceof NumberReferencedDistinctAPIElements) {
            ((NumberReferencedDistinctAPIElements) outboundRef.getMetric()).setValue(1);
        }
        return true;
    }
}
