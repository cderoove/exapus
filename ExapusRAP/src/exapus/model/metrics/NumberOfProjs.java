package exapus.model.metrics;

import exapus.model.forest.ForestElement;
import exapus.model.forest.Member;
import exapus.model.forest.Ref;

import java.util.HashSet;
import java.util.Set;

/**
 * Number of projects that are using APIs
 */

public class NumberOfProjs implements IMetricValue {
    private Set<String> names;
    private Set<String> groupedNames;

    public void addName(String name, ForestElement current, boolean fromDirectMember) {
        if (names == null) names = new HashSet<String>();
        names.add(name);

        if (fromDirectMember) {
            if (groupedNames == null) groupedNames = new HashSet<String>();
            groupedNames.add(name);
        }

        if (current.getParent() != null) {
            ((NumberOfProjs) current.getParent().getMetric(getType())).addName(name, current.getParent(), (current instanceof Member || current instanceof Ref));
        }
    }

    @Override
    public int getValue(boolean groupedPackages) {
        if (groupedPackages) {
            if (groupedNames == null) return 0;
            return groupedNames.size();
        }
        return names == null? 0 : names.size();
    }

    @Override
    public int compareTo(IMetricValue other, boolean groupedPackages) {
        if (other instanceof NumberOfProjs) {
            NumberOfProjs another = (NumberOfProjs) other;
            if (groupedPackages)
                return this.groupedNames.size() < another.groupedNames.size() ? -1 : (this.groupedNames.size() > another.groupedNames.size() ? 1 : 0);
            return this.names.size() < another.names.size() ? -1 : (this.names.size() > another.names.size() ? 1 : 0);
        } else {
            return 0;
        }
    }

    @Override
    public MetricType getType() {
        return MetricType.PROJS;
    }

}
