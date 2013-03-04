package exapus.model.metrics;

import exapus.model.forest.ForestElement;
import exapus.model.forest.Member;
import exapus.model.forest.Ref;

import java.util.HashSet;
import java.util.Set;

/**
 * Number of (distinct) projects types that derive from API types
 */

public class NumberOfSuperAPITypes implements IMetricValue {
    private Set<String> names = new HashSet<String>();
    private Set<String> groupedNames = new HashSet<String>();

    public void addName(String name, ForestElement current, boolean fromDirectMember) {
        names.add(name);
        if (fromDirectMember) groupedNames.add(name);

        if (current.getParent() != null) {
            ((NumberOfSuperAPITypes) current.getParent().getMetric(getType())).addName(name, current.getParent(), (current instanceof Member || current instanceof Ref));
        }
    }

    @Override
    public int getValue(boolean groupedPackages) {

        System.out.println(groupedNames);
        System.out.println(groupedNames.size());

        if (groupedPackages) return groupedNames.size();
        return names.size();
    }

    @Override
    public int compareTo(IMetricValue other, boolean groupedPackages) {
        if (other instanceof NumberOfSuperAPITypes) {
            NumberOfSuperAPITypes another = (NumberOfSuperAPITypes) other;
            if (groupedPackages)
                return this.groupedNames.size() < another.groupedNames.size() ? -1 : (this.groupedNames.size() > another.groupedNames.size() ? 1 : 0);
            return this.names.size() < another.names.size() ? -1 : (this.names.size() > another.names.size() ? 1 : 0);
        } else {
            return 0;
        }
    }

    @Override
    public MetricType getType() {
        return MetricType.API_SUPER;
    }

}