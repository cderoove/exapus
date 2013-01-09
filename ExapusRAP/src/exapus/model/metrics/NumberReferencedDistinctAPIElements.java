package exapus.model.metrics;

import exapus.model.forest.ForestElement;
import exapus.model.forest.Member;
import exapus.model.forest.Ref;

import java.util.HashSet;
import java.util.Set;

/**
 * Sum of referenced distinct API elements below the node
 */

public class NumberReferencedDistinctAPIElements implements IMetric {
    private Set<String> names = new HashSet<String>();
    private Set<String> groupedNames = new HashSet<String>();

    /**
     * Collects names of API elements in the bottom-up fashion, starting from the outbound reference.
     * (Approx. 1500 times faster than if to collect names from getAllReferences() on each node.)
     *
     * @param name API element
     * @param current forest element
     */
    public void addName(String name, ForestElement current, boolean fromDirectMember) {
        names.add(name);
        if (current.getParent() != null) {
            if (current.getParent().getMetric() instanceof NumberReferencedDistinctAPIElements) {
                ((NumberReferencedDistinctAPIElements) current.getParent().getMetric()).addName(name, current.getParent(), (current instanceof Member || current instanceof Ref));
            }
        }
    }

    @Override
    public String getValue(boolean groupedPackages) {
        if (groupedPackages) return Integer.toString(groupedNames.size());
        return Integer.toString(names.size());
    }

    @Override
    public int compareTo(IMetric other, boolean groupedPackages) {
        if (other instanceof NumberReferencedDistinctAPIElements) {
            NumberReferencedDistinctAPIElements another = (NumberReferencedDistinctAPIElements) other;
            if (groupedPackages) return this.groupedNames.size() < another.groupedNames.size() ? -1 : (this.groupedNames.size() > another.groupedNames.size() ? 1 : 0);
            return this.names.size() < another.names.size() ? -1 : (this.names.size() > another.names.size() ? 1 : 0);
        } else {
            return 0;
        }
    }
}
