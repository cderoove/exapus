package exapus.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Iterables;

public abstract class FactForest {

	protected Map<UqName, PackageTree> trees;
	
	private Set<IDeltaListener> listeners;

	private ExapusModel model;

	private Direction direction;
	
	public FactForest(ExapusModel m, Direction d) {
		model = m;
		trees = new HashMap<UqName, PackageTree>();
		listeners = new HashSet<IDeltaListener>();
		direction = d;
	}

	public ExapusModel getModel() {
		return model;
	}

	public Iterable<PackageTree> getPackageTrees() {
		return trees.values();
	}

	public void addListener(IDeltaListener l) {
		listeners.add(l);
	}

	public boolean removeListener(IDeltaListener l) {
		return listeners.remove(l);
	}

	protected void fireUpdate(Object added) {
		DeltaEvent event = new DeltaEvent(added);
		for (IDeltaListener l : listeners)
			l.add(event);
	}

	protected void fireRemove(Object removed) {
		DeltaEvent event = new DeltaEvent(removed);
		for (IDeltaListener l : listeners)
			l.remove(event);
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
				

	public Direction getDirection() {
		return direction;
	}
	
	public void addPackageTree(PackageTree tree) {
		tree.setFactForest(this);
		trees.put(tree.getName(), tree);
		fireUpdate(tree);
	}

	public abstract FactForest getDualFactForest();
	
}
