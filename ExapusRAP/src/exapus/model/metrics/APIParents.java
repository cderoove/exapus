package exapus.model.metrics;

import exapus.model.forest.ForestElement;

/**
 * Number of times an API type is extended/implemented.
 */
public class APIParents implements IMetric {

    private int value;

    public void pp(ForestElement current) {
        this.value++;
        if (current.getParent() != null) {
            ((APIParents) current.getParent().getMetric()).pp(current.getParent());
        }
    }

    @Override
    public String getValue() {
        return Integer.toString(value);
    }

    @Override
    public int compareTo(IMetric other) {
        if (other instanceof APIParents) {
            APIParents another = (APIParents) other;
            return this.value < another.value ? -1 : (this.value > another.value ? 1 : 0);
        } else {
            return 0;
        }
    }
}

