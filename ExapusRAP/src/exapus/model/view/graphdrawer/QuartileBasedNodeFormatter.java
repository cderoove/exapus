package exapus.model.view.graphdrawer;

import exapus.gui.editors.forest.graph.INode;
import exapus.gui.editors.forest.graph.INodeFormatter;
import exapus.model.forest.*;
import exapus.model.metrics.MetricType;
import exapus.model.stats.StatsLevel;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuartileBasedNodeFormatter implements INodeFormatter {

    private MetricType metricType;

    public QuartileBasedNodeFormatter(MetricType metricType) {
        this.metricType = metricType;
    }

    // TODO: this functionality should be somewhere else, probably
    private static enum PENWIDTH {
        ZERO(1), Q1(2), Q2(4), Q3(6), Q4(9), ERROR(20);

        int value;

        private PENWIDTH(int value) {
            this.value = value;
        }
    }

    @Override
    public String label(INode n) {
        ForestElement fe = (ForestElement) n;
        return "\"" + fe.getName().toString() + "\"";
    }

    @Override
    public Iterable<String> decorations(INode n) {
        List<String> decorations = new ArrayList<String>();
        if (n instanceof PackageLayer) {
            decorations.add("shape=box");
            PackageLayer l = (PackageLayer) n;
            if (!l.hasMembers())
                decorations.add("style=\"dashed\"");
            else {
                PENWIDTH penwidth = getPenwidth(l);
                decorations.add("penwidth=" + Integer.toString(penwidth.value));
                if (penwidth == PENWIDTH.ZERO) {
                    decorations.add("style=\"filled\" fillcolor=\"grey92\"");
                }
            }


            return decorations;
        }
        if (n instanceof PackageTree) {
            decorations.add("shape=box");
            decorations.add("style=\"dashed\"");
            return decorations;
        }

        if (n instanceof Member) {
            Member m = (Member) n;
            Element e = m.getElement();
            if (e.declaresType()) {
                decorations.add("shape=oval");
                if (m.isTopLevel()) {
                    PENWIDTH penwidth = getPenwidth(m);
                    decorations.add("penwidth=" + Integer.toString(penwidth.value));
                    if (penwidth == PENWIDTH.ZERO) {
                        decorations.add("style=\"filled\" fillcolor=\"grey92\"");
                    }
                }
            }
            if (e.isMethod())
                throw new UnsupportedOperationException("We don't yet have style for that");
            if (e.isField()) {
                throw new UnsupportedOperationException("We don't yet have style for that");
            }

            return decorations;
        }

        return decorations;
    }

    private PENWIDTH getPenwidth(ForestElement fe) {
        StatsLevel statsLevel = fe instanceof PackageLayer ? StatsLevel.GROUPED_PACKAGES : StatsLevel.TOP_LEVEL_TYPES;
        Map<StatsLevel, DescriptiveStatistics> map = fe.getParentFactForest().getStats().get(metricType);
        if(map == null) {
        	System.err.println("Error computing pen width for: " + fe.toString());
        	return PENWIDTH.ERROR;
        }
        DescriptiveStatistics ds = map.get(statsLevel);
        if (ds.getN() == 1) return PENWIDTH.Q1;

        int value = fe.getMetric(metricType).getValue(true);
        if (value == 0) return PENWIDTH.ZERO;

        // TODO: this functionality should be somewhere else, probably
        PENWIDTH result;
        if (value < ds.getPercentile(50)) {
            if (value < ds.getPercentile(25)) result = PENWIDTH.Q1;
            else result = PENWIDTH.Q2;
        } else {
            if (value < ds.getPercentile(75)) result = PENWIDTH.Q3;
            else result = PENWIDTH.Q4;
        }

/*
        System.err.printf("ds = %d [%.2f %.2f %.2f %.2f %.2f]\n",
                ds.getN(), ds.getMin(), ds.getPercentile(25), ds.getPercentile(50), ds.getPercentile(75), ds.getMax());
        System.err.printf("value = %d, quartile = %s\n", value, result.name());
*/

        return result;
    }

    @Override
    public String getIdentifier(INode n) {
        return Integer.toString(System.identityHashCode(n));
    }

}
