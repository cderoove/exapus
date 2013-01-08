package exapus.model.metrics;

import exapus.model.forest.ForestElement;

/**
 * Direct sum of all references below the node
 */

public class TotalNumberAPIReferences implements IMetric {

    private int value = 0;

    public String getValue() {
        return Integer.toString(value);
    }

    /**
     * Increases the metric by one. Fires the increase up the tree.
     * (By one, since the changes start at the outbound reference, which counts itself as one.)
     *
     * Using Iterables.size() on getAllReferences() turned out to be much slower (~5000 times)
     *
     * @param current forest element
     */
    public void pp(ForestElement current) {
        this.value++;
        if (current.getParent() != null) {
            ((TotalNumberAPIReferences) current.getParent().getMetric()).pp(current.getParent());
        }
    }

    @Override
    public int compareTo(IMetric other) {
        if (other instanceof TotalNumberAPIReferences) {
            TotalNumberAPIReferences another = (TotalNumberAPIReferences) other;
            return this.value < another.value ? -1 : (this.value > another.value ? 1 : 0);
        } else {
            return 0;
        }
    }
}
