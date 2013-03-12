package exapus.model.forest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.google.common.collect.Iterables;

import exapus.model.Observable;
import exapus.model.metrics.MetricType;
import exapus.model.stats.StatsLevel;
import exapus.model.store.Store;
import exapus.model.tags.Cloud;
import exapus.model.tags.Tag;
import exapus.model.visitors.IForestVisitor;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public abstract class FactForest extends Observable {

	
    private Map<MetricType, Map<StatsLevel, DescriptiveStatistics>> stats;

	protected Map<UqName, PackageTree> trees;
	
	private Map<ForestElement, Cloud> element2tags;
	private Map<Ref, Cloud> ref2dualtags;


	private ExapusModel model;

	private Direction direction;
	

	public FactForest(ExapusModel m, Direction d) {
		model = m;
		trees = new HashMap<UqName, PackageTree>();
		direction = d;
        stats = new HashMap<MetricType, Map<StatsLevel, DescriptiveStatistics>>();
        element2tags = new WeakHashMap<ForestElement, Cloud>();
        ref2dualtags = new WeakHashMap<Ref, Cloud>();
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
	
	public boolean removePackageTree(PackageTree tree) {
		return (trees.remove(tree.getName()) != null);
	}
		
	public PackageTree getOrAddPackageTree(PackageTree original) {
		UqName name = original.getName();
		PackageTree tree = getPackageTree(name);
		if(tree == null) {
			tree = PackageTree.from(original);
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
    		return getCorrespondingForestElement(ancestors, element);
    	return null;
    }

    public ForestElement getCorrespondingForestElement(boolean copyWhenMissing, ForestElement element) {
    	if(element instanceof PackageTree) {
    		UqName name = element.getName();
    		if(copyWhenMissing) 
    			return getOrAddPackageTree((PackageTree) element);
    		else
    			return getPackageTree(name);
    	}
    	Iterator<ForestElement> ancestors = element.getAncestors().iterator();
    	if(ancestors.hasNext())
    		return getCorrespondingForestElement(copyWhenMissing, ancestors, element);
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

    
    public Object[] getCorrespondingForestElements(boolean copyWhenMissing, Object[] elements) {
    	ArrayList<Object> corresponding = new ArrayList<Object>(elements.length);
    	for(Object element : elements) {
    		if(element instanceof ForestElement) {
    			ForestElement correspondingElement = getCorrespondingForestElement(copyWhenMissing, (ForestElement) element);
    			if(correspondingElement != null)
    				corresponding.add(correspondingElement);
    		}
    	}
    	return corresponding.toArray();
    }

	public ForestElement getCorrespondingForestElement(boolean copyWhenMissing, Iterator<ForestElement> ancestors, ForestElement element) {
		ForestElement ancestor = ancestors.next();
		ForestElement correspondingAncestor = getCorrespondingForestElement(copyWhenMissing, ancestor);
		if(correspondingAncestor == null)
			return null;
		if(ancestors.hasNext())
			return correspondingAncestor.getCorrespondingForestElement(copyWhenMissing, ancestors, element);
		return correspondingAncestor.getCorrespondingForestElement(copyWhenMissing, element);
	}

	public ForestElement getCorrespondingForestElement(Iterator<ForestElement> ancestors, ForestElement element) {
		ForestElement ancestor = ancestors.next();
		ForestElement correspondingAncestor = getCorrespondingForestElement(ancestor);
		if(correspondingAncestor == null)
			return null;
		if(ancestors.hasNext())
			return correspondingAncestor.getCorrespondingForestElement(ancestors, element);
		return correspondingAncestor.getCorrespondingForestElement(element);
	}
	
	public boolean hasChildren() {
		return !trees.isEmpty();
	}

	public Cloud getTagsFor(ForestElement forestElement) {
		Cloud cloud = element2tags.get(forestElement);
		if(cloud != null)
			return cloud;
		return Cloud.EMPTY_CLOUD;
	}
	
	public Cloud getDualTagsFor(Ref ref) {
		Cloud cloud = ref2dualtags.get(ref);
		if(cloud != null)
			return cloud;
		return Cloud.EMPTY_CLOUD;
	}

	public void intializeDualTagsForFrom(Ref destination, FactForest sourceForest, Ref source, FactForest dualForest, Ref dual) {
		Cloud cloud = dualForest.getTagsFor(dual); //dual will still be the original one, but hashCode() override should return the updated reference in the dualForest
		ref2dualtags.put(destination, cloud);
	}
	
	public void intializeTagsForFrom(ForestElement destination, FactForest sourceForest, ForestElement element) {
		Cloud cloud = sourceForest.getTagsFor(element);
		element2tags.put(destination, cloud);
	}

	public void addTag(ForestElement forestElement, Tag tag) {
		Cloud before = this.getTagsFor(forestElement);
		Cloud after = Store.getCurrent().getOrRegisterExtendedCloud(before, tag);
		element2tags.put(forestElement, after);
	}

	public void addDualTag(Ref ref, Tag tag) {
		Cloud before = this.getDualTagsFor(ref);
		Cloud after = Store.getCurrent().getOrRegisterExtendedCloud(before, tag);
		ref2dualtags.put(ref, after);
	}

	
	public boolean hasTag(ForestElement forestElement, Tag tag) {
		return getTagsFor(forestElement).hasTag(tag);
	}
	
	
		
}
