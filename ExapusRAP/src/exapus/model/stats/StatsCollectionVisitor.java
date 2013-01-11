package exapus.model.stats;

import exapus.model.forest.*;
import exapus.model.metrics.MetricType;
import exapus.model.view.View;
import exapus.model.visitors.IForestVisitor;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.HashMap;
import java.util.Set;

public class StatsCollectionVisitor implements IForestVisitor {
    private View view;
    private boolean initialized = false;

    public StatsCollectionVisitor(View view) {
        this.view = view;
    }

    private void initStats(ForestElement fe, Set<MetricType> metrics) {
        if (initialized) return;

        initialized = true;
        for (MetricType metric : metrics) {
            if (!fe.getParentFactForest().getStats().containsKey(metric)) {
                HashMap<StatsLevel, DescriptiveStatistics> stats = new HashMap<StatsLevel, DescriptiveStatistics>();
                stats.put(StatsLevel.GROUPED_PACKAGES, new DescriptiveStatistics());
                stats.put(StatsLevel.TOP_LEVEL_TYPES, new DescriptiveStatistics());
                fe.getParentFactForest().getStats().put(metric, stats);
            }
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
        return true;
    }

    @Override
    public boolean visitPackageLayer(PackageLayer packageLayer) {
        if (packageLayer.hasMembers()) {
            Set<MetricType> registeredMetrics = packageLayer.getRegisteredMetrics();
            initStats(packageLayer, registeredMetrics);

            for (MetricType metric : registeredMetrics) {
                packageLayer.getParentFactForest().getStats().get(metric).get(StatsLevel.GROUPED_PACKAGES).addValue(packageLayer.getMetric(metric).getValue(true));
            }
        }

        return true;
    }

    @Override
    public boolean visitMember(Member member) {
        if (member.isTopLevel()) {
            for (MetricType metric : member.getRegisteredMetrics()) {
                member.getParentFactForest().getStats().get(metric).get(StatsLevel.TOP_LEVEL_TYPES).addValue(member.getMetric(metric).getValue(true));
            }
        }

        return false;
    }

    @Override
    public boolean visitInboundReference(InboundRef inboundRef) {
        return false;
    }

    @Override
    public boolean visitOutboundReference(OutboundRef outboundRef) {
        return false;
    }
}
