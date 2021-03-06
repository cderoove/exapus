package exapus.model.metrics;

import exapus.model.forest.ForestElement;
import exapus.model.forest.Member;
import exapus.model.forest.Ref;

/**
 * Direct sum of all references below the node
 */

public class TotalNumberAPIReferences implements IMetricValue {

    private int value = 0;
    private int groupedValue = 0;

    public int getValue(boolean groupedPackages) {
        if (groupedPackages) return groupedValue;
        return value;
    }

    /**
     * Increases the metric by one. Fires the increase up the tree.
     * (By one, since the changes start at the outbound reference, which counts itself as one.)
     *
     * Using Iterables.size() on getAllReferences() turned out to be much slower (~5000 times)
     *
     * @param current forest element
     */
    public void pp(ForestElement current, boolean fromDirectMember) {
        this.value++;
        if (fromDirectMember) this.groupedValue++;

        if (current.getParent() != null) {
            ((TotalNumberAPIReferences) current.getParent().getMetric(getType())).pp(current.getParent(), (current instanceof Member || current instanceof Ref));
        }
    }

    @Override
    public int compareTo(IMetricValue other, boolean groupedPackages) {
        if (other instanceof TotalNumberAPIReferences) {
            TotalNumberAPIReferences another = (TotalNumberAPIReferences) other;
            if (groupedPackages) return this.groupedValue < another.groupedValue ? -1 : (this.groupedValue > another.groupedValue ? 1 : 0);
            return this.value < another.value ? -1 : (this.value > another.value ? 1 : 0);
        } else {
            return 0;
        }
    }

    @Override
    public MetricType getType() {
        return MetricType.API_REFS;
    }

}
