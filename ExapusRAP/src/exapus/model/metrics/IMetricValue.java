package exapus.model.metrics;

public interface IMetricValue /*extends Comparable<IMetricValue>*/ {

    int getValue(boolean groupedPackages);

    int compareTo(IMetricValue other, boolean groupedPackages);

    MetricType getType();
}
