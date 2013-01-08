package exapus.model.metrics;

public interface IMetric extends Comparable<IMetric> {
    /**
     * @return formatted value
     */
    String getValue();

    int compareTo(IMetric other);
}
