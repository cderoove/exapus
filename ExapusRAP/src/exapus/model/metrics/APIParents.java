package exapus.model.metrics;

import exapus.model.forest.ForestElement;
import exapus.model.forest.Member;
import exapus.model.forest.Ref;

/**
 * Number of times an API type is extended/implemented.
 */
public class APIParents implements IMetric {

    private int value;
    private int groupedValue;

    public void pp(ForestElement current, boolean fromDirectMember) {
        this.value++;
        if (fromDirectMember) this.groupedValue++;

        if (current.getParent() != null) {
            ((APIParents) current.getParent().getMetric()).pp(current.getParent(), (current instanceof Member || current instanceof Ref));
        }
    }

    @Override
    public String getValue(boolean groupedPackages) {
        if (groupedPackages) return Integer.toString(groupedValue);
        return Integer.toString(value);
    }

    @Override
    public int compareTo(IMetric other, boolean groupedPackages) {
        if (other instanceof APIParents) {
            APIParents another = (APIParents) other;
            if (groupedPackages) return this.groupedValue < another.groupedValue ? -1 : (this.groupedValue > another.groupedValue ? 1 : 0);
            return this.value < another.value ? -1 : (this.value > another.value ? 1 : 0);
        } else {
            return 0;
        }
    }
}

