package exapus.model.forest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Iterables;

import exapus.model.Observable;
import exapus.model.metrics.MetricType;
import exapus.model.stats.StatsLevel;
import exapus.model.visitors.IForestVisitor;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public abstract class FactForest extends Observable {

    private Map<MetricType, Map<StatsLevel, DescriptiveStatistics>> stats;

	protected Map<UqName, PackageTree> trees;

	private ExapusModel model;

	private Direction direction;

	public FactForest(ExapusModel m, Direction d) {
		model = m;
		trees = new HashMap<UqName, PackageTree>();
		direction = d;
        stats = new HashMap<MetricType, Map<StatsLevel, DescriptiveStatistics>>();
	}

	public ExapusModel getModel() {
		return model;
	}

	public Iterable<PackageTree> getPackageTrees() {
		return trees.values();
	}

	public Iterable<Member> getAllMembers() {
		Iterable<Member> members = new ArrayList<Member>();
		for(PackageTree t : getPackageTrees()) {
			members = Iterables.concat(members, t.getAllMembers());
		}
		return members;
	}

	public Iterable<Ref> getAllReferences() {
		Iterable<Ref> references = new ArrayList<Ref>();
		for(Member m : getAllMembers()) {
			references = Iterables.concat(references, m.getAllReferences());
		}
		return references;
	}

	
	public Iterable<PackageLayer> getAllPackageLayers() {
		Iterable<PackageLayer> layers = new ArrayList<PackageLayer>();
		for(PackageTree t : getPackageTrees()) {
			layers = Iterables.concat(layers, t.getAllPackageLayers());
		}
		return layers;
	}

	public Direction getDirection() {
		return direction;
	}

	public void addPackageTree(PackageTree tree) {
		tree.setFactForest(this);
		trees.put(tree.getName(), tree);
		fireUpdate(tree);
	}
	
	public PackageTree getPackageTree(UqName name) {
		return trees.get(name);
	}
	
	public PackageTree getOrAddPackageTree(UqName name) {
		PackageTree tree = getPackageTree(name);
		if(tree == null) {
			tree = new PackageTree(name);
			addPackageTree(tree);
		}
		return tree;
	}

	public abstract FactForest getDualFactForest();

	public abstract void acceptVisitor(IForestVisitor v);

    public Map<MetricType, Map<StatsLevel, DescriptiveStatistics>> getStats() {
        return stats;
    }
    
    public ForestElement getCorrespondingForestElement(ForestElement element) {
    	if(element instanceof PackageTree)
    		return getPackageTree(element.getName());
    	Iterator<ForestElement> ancestors = element.getAncestors().iterator();
    	if(ancestors.hasNext())
    		return getCorrespondingForestElement(ancestors,element);
    	return null;
    }
    
    public Object[] getCorrespondingForestElements(Object[] elements) {
    	ArrayList<Object> corresponding = new ArrayList<Object>(elements.length);
    	for(Object element : elements) {
    		if(element instanceof ForestElement) {
    			ForestElement correspondingElement = getCorrespondingForestElement((ForestElement) element);
    			if(correspondingElement != null)
    				corresponding.add(correspondingElement);
    		}
    	}
    	return corresponding.toArray();
    }

	ForestElement getCorrespondingForestElement(Iterator<ForestElement> ancestors, ForestElement element) {
		ForestElement originalTree = ancestors.next();
		PackageTree correspondingTree = getPackageTree(originalTree.getName());
		if(correspondingTree == null)
			return null;
		if(ancestors.hasNext())
			return correspondingTree.getCorrespondingForestElement(ancestors, element);
		return correspondingTree.getCorrespondingForestElement(element);
	}
	
	
}
