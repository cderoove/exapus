package exapus.model.forest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.google.common.collect.Iterables;

import exapus.model.IDeltaListener;
import exapus.model.Observable;
import exapus.model.visitors.IForestVisitor;

public abstract class FactForest extends Observable {

	protected Map<UqName, PackageTree> trees;

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

	public abstract void acceptVisitor(IForestVisitor v);


}
