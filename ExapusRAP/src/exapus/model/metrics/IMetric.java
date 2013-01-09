package exapus.model.metrics;

public interface IMetric /*extends Comparable<IMetric>*/ {
    /**
     * @return formatted value
     */
    String getValue(boolean groupedPackages);

    int compareTo(IMetric other, boolean groupedPackages);
}
