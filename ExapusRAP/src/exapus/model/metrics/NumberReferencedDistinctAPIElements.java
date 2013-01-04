package exapus.model.metrics;

/**
 * Sum of referenced distinct API elements below the node
 */

public class NumberReferencedDistinctAPIElements implements IMetric {
    private int value;

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return Integer.toString(value);
    }
}
