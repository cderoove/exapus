package exapus.model.metrics;

import exapus.model.view.View;
import exapus.model.visitors.IForestVisitor;

import java.lang.reflect.Constructor;

/**
 * Keeping together metric visitors
 */
public enum MetricType {
    API_REFS(TotalNumberAPIReferencesVisitor.class.getCanonicalName(),
            "#refs",
            "Total references to APIs from projects"),
    API_ELEM(NumberReferencedDistinctAPIElementsVisitor.class.getCanonicalName(),
            "#elems",
            "Number of distinct API elements referenced by projects"),
    API_CHILDREN(APIChildrenVisitor.class.getCanonicalName(),
            "#derives",
            "Total derivations from API types"),
    API_SUPER(NumberOfSuperAPITypesVisitor.class.getCanonicalName(),
            "#super",
            "Number of API types from which projects derive"),
    API_SUB(NumberOfSubAPITypesVisitor.class.getCanonicalName(),
            "#sub",
            "Number of project types deriving from API types"),
    ALL(null, "", "");

    private String qName;
    private String shortName;
    private String toolTip;

    private MetricType(String qName, String shortName, String toolTip) {
        this.qName = qName;
        this.shortName = shortName;
        this.toolTip = toolTip;
    }

    public IForestVisitor getVisitor(View view) {
        try {
            Class<?> clazz = Class.forName(qName);
            Constructor constructor = clazz.getConstructor(new Class[]{View.class});
            return (IForestVisitor) constructor.newInstance(view);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getShortName() {
        return shortName;
    }

    public String getToolTipText() {
        return toolTip;
    }

    public static MetricType[] supportedMetrics(boolean withoutAll) {
        if (withoutAll) return new MetricType[]{API_REFS, API_ELEM, API_CHILDREN, API_SUPER, API_SUB};
        return MetricType.class.getEnumConstants();
    }

    public static MetricType defaultValue(boolean withoutAll) {
        if (withoutAll) return API_REFS;
        return ALL;
    }

    public static boolean supportsMetric(boolean renderable, MetricType metricType) {
        MetricType[] metricTypes = supportedMetrics(renderable);
        for (MetricType type : metricTypes) {
            if (metricType.equals(type)) return true;
        }
        return false;
    }
}
