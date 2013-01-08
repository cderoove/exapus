package exapus.model.metrics;

import exapus.model.forest.ForestElement;

/**
 * Number of times when an API interface/class is extended or implemented.
 * (Non-distinct)
 */
public class APIChildren implements IMetric {

    private int value;

    public void pp(ForestElement current) {
        this.value++;
        if (current.getParent() != null) {
            ((APIChildren) current.getParent().getMetric()).pp(current.getParent());
        }
    }

    @Override
    public String getValue() {
        return Integer.toString(value);
    }

    @Override
    public int compareTo(IMetric other) {
        if (other instanceof APIChildren) {
            APIChildren another = (APIChildren) other;
            return this.value < another.value ? -1 : (this.value > another.value ? 1 : 0);
        } else {
            return 0;
        }
    }
}
