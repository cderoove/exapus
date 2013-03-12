package exapus.model.view.graphdrawer;

import exapus.gui.editors.forest.graph.INode;
import exapus.gui.editors.forest.graph.INodeFormatter;
import exapus.gui.editors.forest.graph.Node;
import exapus.model.forest.*;
import exapus.model.stats.StatsLevel;
import exapus.model.view.View;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuartileBasedNodeFormatter implements INodeFormatter {

    private View view;

    public QuartileBasedNodeFormatter(View view) {
        this.view = view;
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
        ForestElement fe = null;
        if (n instanceof Node) {
            fe = ((Node) n).getFe();
        } else {
            fe = (ForestElement) n;
        }
        return "\"" + fe.getName().toString() + "\"";
    }

    @Override
    public Iterable<String> decorations(INode n) {

        ForestElement fe = null;
        Node.SpecialCase spCase = null;

        if (n instanceof Node) {
            Node node = (Node) n;
            fe = node.getFe();
            spCase = node.getSpCase();
        } else {
            fe = (ForestElement) n;
        }

        //System.err.println("n = " + n.getClass().getCanonicalName());
        //System.err.println("fe = " + fe.getQName().toString());
        //System.err.println("\tspCase = " + spCase);

        List<String> decorations = new ArrayList<String>();

        decorations.add("style=\"filled\" fillcolor=\"grey92\"");

        if (fe instanceof PackageLayer) {
            PackageLayer l = (PackageLayer) fe;

            decorations.add("shape=box");
            if (spCase != null) {
                switch (spCase) {
                    case TOP_LEVEL_TAG_WO_CHILDREN:
                        decorations.add("shape=triangle");
                }
            }

            PENWIDTH penwidth = getPenwidth(l, spCase);
            decorations.add("penwidth=" + Integer.toString(penwidth.value));

            if (penwidth == PENWIDTH.ZERO) {
                if (!l.hasMembers()) {
                    decorations.add("style=\"dashed\"");
                } else {
                    decorations.add("fillcolor=\"white\"");
                }
            }

            //System.err.println("\tlayer decorations = " + decorations.toString());
            return decorations;
        }

        if (fe instanceof PackageTree) {
            decorations.add("shape=box");
            decorations.add("style=\"dashed\"");
            //System.err.println("\ttree decorations = " + decorations.toString());
            return decorations;
        }

        if (fe instanceof Member) {
            Member m = (Member) fe;
            Element e = m.getElement();
            if (e.declaresType()) {
                decorations.add("shape=oval");
                if (m.isTopLevel()) {
                    PENWIDTH penwidth = getPenwidth(m, spCase);
                    decorations.add("penwidth=" + Integer.toString(penwidth.value));
                    if (penwidth == PENWIDTH.ZERO) {
                        decorations.add("style=\"filled\" fillcolor=\"grey92\"");
                    }
                }
            }
            if (e.isMethod()) {
                throw new UnsupportedOperationException("We don't yet have style for that");
            }
            if (e.isField()) {
                throw new UnsupportedOperationException("We don't yet have style for that");
            }

            //System.err.println("\tmember decorations = " + decorations.toString());
            return decorations;
        }

        //System.err.println("\tWTF decorations = " + decorations.toString());
        return decorations;
    }

    private PENWIDTH getPenwidth(ForestElement fe, Node.SpecialCase spCase) {
        StatsLevel statsLevel = fe instanceof PackageLayer ? StatsLevel.GROUPED_PACKAGES : StatsLevel.TOP_LEVEL_TYPES;
        Map<StatsLevel, DescriptiveStatistics> map = fe.getParentFactForest().getStats().get(view.getMetricType());
        if (map == null) {
            System.err.println("Error computing pen width for: " + fe.toString());
            return PENWIDTH.ERROR;
        }

        DescriptiveStatistics ds = map.get(statsLevel);
        //if (view.isGraphDetailsOnlyWithUsage()) {
        DescriptiveStatistics dsWoZeros = new DescriptiveStatistics();
        for (double v : ds.getValues()) {
            if (v == 0) continue;
            dsWoZeros.addValue(v);
        }
        ds = dsWoZeros;
        //}

/*        System.err.println("ds = ");
        for (double v : ds.getValues()) {
            System.err.print(v + ", ");
        }
        System.err.println("");*/

        if (ds.getN() == 1) return PENWIDTH.Q1;

        //System.err.println("statsLevel = " + statsLevel);
        int value = 0;
        if (spCase != null) {
            switch (spCase) {
                case TOP_LEVEL_TAG_WO_CHILDREN:
                    //System.err.println("\t!!!Special case: top level tag w/o direct children");
                    value = fe.getMetric(view.getMetricType()).getValue(false);
            }
        } else {
            value = fe.getMetric(view.getMetricType()).getValue(StatsLevel.GROUPED_PACKAGES.equals(statsLevel));
        }
/*
        System.err.println("Just for fun");
        System.err.println("fe.getMetric(view.getMetricType()).getValue(false) = " + fe.getMetric(view.getMetricType()).getValue(false));
        System.err.println("fe.getMetric(view.getMetricType()).getValue(true) = " + fe.getMetric(view.getMetricType()).getValue(true));*/

        //System.err.println("value = " + value);
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
