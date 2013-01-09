package exapus.gui.editors.forest.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;

import com.google.common.collect.Iterables;

import exapus.gui.util.Util;
import exapus.model.AddDeltaEvent;
import exapus.model.DeltaEvent;
import exapus.model.IDeltaListener;
import exapus.model.RemoveDeltaEvent;
import exapus.model.forest.FactForest;
import exapus.model.forest.ForestElement;
import exapus.model.forest.Member;
import exapus.model.forest.MemberContainer;
import exapus.model.Observable;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.Ref;

public class ForestTreeContentProvider implements ITreeContentProvider, IDeltaListener {

	protected TreeViewer viewer;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer) viewer;
		if (oldInput != null) {
			removeListenerFrom((Observable) oldInput);
		}
		if (newInput != null) {
			addListenerTo((Observable) newInput);
		}
	}

	private void removeListenerFrom(Observable oldInput) {
		oldInput.removeListener(this);
	}

	private void addListenerTo(Observable newInput) {
		newInput.addListener(this);
	}

	@Override
	public Object[] getElements(Object inputElement) {
		// build roots of tree
		if (inputElement instanceof FactForest)
			return Iterables.toArray(((FactForest) inputElement).getPackageTrees(), PackageTree.class);
		else
			return null;
	}

	@Override
	//TODO: getChildren() should be implemented on ForestElement, preferably in a thread-safe manner
	public  Object[] getChildren(Object parentElement) {
		if (parentElement instanceof PackageTree)
			return Iterables.toArray(((PackageTree) parentElement).getLayers(), PackageLayer.class);
		if (parentElement instanceof PackageLayer) {
			PackageLayer layer = (PackageLayer) parentElement;
			Iterable<Member> members = layer.getMembers();
			Iterable<PackageLayer> layers = layer.getLayers();
			return Iterables.toArray(Iterables.concat(members, layers), MemberContainer.class);
		}
		if (parentElement instanceof Member) {
			Member member = (Member) parentElement;
			Iterable<Member> members = member.getMembers();
			Iterable<Ref> references = member.getReferences();
			return Iterables.toArray(Iterables.concat(members, references), Object.class);
		}

		return null;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof ForestElement) {
			ForestElement fe = (ForestElement) element;
			return fe.getParent();
		}
		return null;

	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Ref)
			return false;
		return getChildren(element).length > 0;
	}



	@Override
	public void delta(DeltaEvent event) {
		final ForestElement element = (ForestElement) event.receiver();
		if(event instanceof AddDeltaEvent) {
			Util.asyncUIThreadIfWidgetNotDisposed(viewer.getControl(), new Runnable() {
				public void run() {
					if (element instanceof Ref)
						viewer.refresh(element.getParentMember(), true);
					else if (element instanceof PackageTree)
						viewer.refresh(element, true);
					else
						viewer.refresh(element.getParentPackageLayer(), true);
				}
			});
		}
		if(event instanceof RemoveDeltaEvent) {
			Util.asyncUIThreadIfWidgetNotDisposed(viewer.getControl(), new Runnable() {
				public void run() {
					if (element instanceof PackageTree)
						viewer.refresh();
					else
						delta(new AddDeltaEvent(element));
				}
			});
		}

	}

}
