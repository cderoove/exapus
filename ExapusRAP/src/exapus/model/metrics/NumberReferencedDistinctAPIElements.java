package exapus.model.metrics;

import exapus.model.forest.ForestElement;
import exapus.model.forest.Member;
import exapus.model.forest.Ref;

import java.util.HashSet;
import java.util.Set;

/**
 * Sum of referenced distinct API elements below the node
 */

public class NumberReferencedDistinctAPIElements implements IMetricValue {
    private Set<String> names;
    private Set<String> groupedNames;

    /**
     * Collects names of API elements in the bottom-up fashion, starting from the outbound reference.
     * (Approx. 1500 times faster than if to collect names from getAllReferences() on each node.)
     *
     * @param name    API element
     * @param current forest element
     */
    public void addName(String name, ForestElement current, boolean fromDirectMember) {
        if (names == null) names = new HashSet<String>();
        names.add(name);

        if (fromDirectMember) {
            if (groupedNames == null) groupedNames = new HashSet<String>();
            groupedNames.add(name);
        }

        if (current.getParent() != null) {
            ((NumberReferencedDistinctAPIElements) current.getParent().getMetric(getType())).addName(name, current.getParent(), (current instanceof Member || current instanceof Ref));
        }
    }

    @Override
    public int getValue(boolean groupedPackages) {
        if (groupedPackages) {
            if (groupedNames == null) return 0;
            return groupedNames.size();
        }
        return names == null? 0 : names.size();
    }

    @Override
    public int compareTo(IMetricValue other, boolean groupedPackages) {
        if (other instanceof NumberReferencedDistinctAPIElements) {
            NumberReferencedDistinctAPIElements another = (NumberReferencedDistinctAPIElements) other;
            if (groupedPackages)
                return this.groupedNames.size() < another.groupedNames.size() ? -1 : (this.groupedNames.size() > another.groupedNames.size() ? 1 : 0);
            return this.names.size() < another.names.size() ? -1 : (this.names.size() > another.names.size() ? 1 : 0);
        } else {
            return 0;
        }
    }

    @Override
    public MetricType getType() {
        return MetricType.API_ELEM;
    }

}
