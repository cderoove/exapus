package exapus.model.metrics;

import exapus.model.forest.ForestElement;

import java.util.HashSet;
import java.util.Set;

/**
 * Sum of referenced distinct API elements below the node
 */

public class NumberReferencedDistinctAPIElements implements IMetric {
    private Set<String> names = new HashSet<String>();

    /**
     * Collects names of API elements in the bottom-up fashion, starting from the outbound reference.
     * (Approx. 1500 times faster than if to collect names from getAllReferences() on each node.)
     *
     * @param name API element
     * @param current forest element
     */
    public void addName(String name, ForestElement current) {
        names.add(name);
        if (current.getParent() != null) {
            if (current.getParent().getMetric() instanceof NumberReferencedDistinctAPIElements) {
                ((NumberReferencedDistinctAPIElements) current.getParent().getMetric()).addName(name, current.getParent());
            }
        }
    }

    @Override
    public String getValue() {
        return Integer.toString(names.size());
    }
}
