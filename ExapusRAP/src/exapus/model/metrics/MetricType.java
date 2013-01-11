package exapus.model.metrics;

import exapus.model.view.View;
import exapus.model.visitors.IForestVisitor;

import java.lang.reflect.Constructor;

/**
 * Keeping together metric visitors
 */
public enum MetricType {
    API_REFS(TotalNumberAPIReferencesVisitor.class.getCanonicalName(), "#APIRefs"),
    API_ELEM(NumberReferencedDistinctAPIElementsVisitor.class.getCanonicalName(), "#APIElem"),
    API_CHILDREN(APIChildrenVisitor.class.getCanonicalName(), "#APIDerived"),
    ALL(null, "");

    private String qName;
    private String shortName;

    private MetricType(String qName, String shortName) {
        this.qName = qName;
        this.shortName = shortName;
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

    public static MetricType[] supportedMetrics(boolean withoutAll) {
        if (withoutAll) return new MetricType[]{API_REFS, API_ELEM, API_CHILDREN};
        return MetricType.class.getEnumConstants();
    }

    public static MetricType defaultValue(boolean withoutAll) {
        if (withoutAll) return API_REFS;
        return ALL;
    }
}
