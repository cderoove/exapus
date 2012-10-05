package exapus.gui.views.forest;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;

import com.google.common.collect.Iterables;

import exapus.model.DeltaEvent;
import exapus.model.FactForest;
import exapus.model.ForestElement;
import exapus.model.IDeltaListener;
import exapus.model.Member;
import exapus.model.MemberContainer;
import exapus.model.PackageLayer;
import exapus.model.PackageTree;
import exapus.model.Ref;

public class FactForestTreeContentProvider implements ITreeContentProvider, IDeltaListener {

	protected TreeViewer viewer;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer) viewer;
		if (oldInput != null) {
			removeListenerFrom((FactForest) oldInput);
		}
		if (newInput != null) {
			addListenerTo((FactForest) newInput);
		}
	}

	private void removeListenerFrom(FactForest oldInput) {
		oldInput.removeListener(this);
	}

	private void addListenerTo(FactForest newInput) {
		newInput.addListener(this);
	}

	@Override
	public PackageTree[] getElements(Object inputElement) {
		// build roots of tree
		if (inputElement instanceof FactForest)
			return Iterables.toArray(((FactForest) inputElement).getPackageTrees(), PackageTree.class);
		else
			return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
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

	public void asyncExecIfNotDisposed(Runnable r) {
		Tree tree = viewer.getTree();
		if (!tree.isDisposed()) {
			Display display = tree.getDisplay();
			display.asyncExec(r);
		}
	}

	@Override
	public void add(DeltaEvent event) {
		final ForestElement element = (ForestElement) event.receiver();
		asyncExecIfNotDisposed(new Runnable() {
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

	@Override
	public void remove(final DeltaEvent event) {
		final ForestElement element = (ForestElement) event.receiver();
		asyncExecIfNotDisposed(new Runnable() {
			public void run() {
				if (element instanceof PackageTree)
					viewer.refresh();
				else
					add(event);
			}
		});
	}
}
