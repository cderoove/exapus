package exapus.model.metrics;

import exapus.model.view.View;
import exapus.model.visitors.IForestVisitor;

import java.lang.reflect.Constructor;

/**
 * Keeping together metric visitors
 */
public enum Metrics {
    API_REFS(TotalNumberAPIReferencesVisitor.class.getCanonicalName(), "#APIRefs"),
    API_ELEM(NumberReferencedDistinctAPIElementsVisitor.class.getCanonicalName(), "#APIElem"),
    API_CHILDREN(APIChildrenVisitor.class.getCanonicalName(), "#APIDerived"),
    ALL(null, "");

    private String qName;
    private String shortName;

    private Metrics(String qName, String shortName) {
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

    public static Metrics[] supportedMetrics() {
        return Metrics.class.getEnumConstants();
    }

    public static Metrics defaultValue() {
        return ALL;
    }
}
