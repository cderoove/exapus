package exapus.model.metrics;

import exapus.model.forest.ForestElement;
import exapus.model.forest.Member;
import exapus.model.forest.Ref;

/**
 * Number of times when an API interface/class is extended or implemented.
 * (Non-distinct)
 */
public class APIChildren implements IMetricValue {

    private int value;
    private int groupedValue;

    public void pp(ForestElement current, boolean fromDirectMember) {
        this.value++;
        if (fromDirectMember) this.groupedValue++;

        if (current.getParent() != null) {
            ((APIChildren) current.getParent().getMetric(getType())).pp(current.getParent(), (current instanceof Member || current instanceof Ref));
        }
    }

    @Override
    public int getValue(boolean groupedPackages) {
        if (groupedPackages) return groupedValue;
        return value;
    }

    @Override
    public int compareTo(IMetricValue other, boolean groupedPackages) {
        if (other instanceof APIChildren) {
            APIChildren another = (APIChildren) other;
            if (groupedPackages) return this.groupedValue < another.groupedValue ? -1 : (this.groupedValue > another.groupedValue ? 1 : 0);
            return this.value < another.value ? -1 : (this.value > another.value ? 1 : 0);
        } else {
            return 0;
        }
    }

    @Override
    public MetricType getType() {
        return MetricType.API_CHILDREN;
    }
}
