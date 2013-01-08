package exapus.model.metrics;

import exapus.model.visitors.IForestVisitor;

/**
 * Keeping together metric visitors
 */
public enum Metrics {
    API_REFS(TotalNumberAPIReferencesVisitor.class.getCanonicalName(), "#APIRefs"),
    API_ELEM(NumberReferencedDistinctAPIElementsVisitor.class.getCanonicalName(), "#APIElem"),
    API_CHILDREN(APIChildrenVisitor.class.getCanonicalName(), "#APIChildren"),
    API_PARENTS(APIParentsVisitor.class.getCanonicalName(), "#APIParents");

    private String qName;
    private String shortName;

    private Metrics(String qName, String shortName) {
        this.qName = qName;
        this.shortName = shortName;
    }

    public IForestVisitor getVisitor() {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(qName);
            return (IForestVisitor) clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getShortName() {
        return shortName;
    }
}
