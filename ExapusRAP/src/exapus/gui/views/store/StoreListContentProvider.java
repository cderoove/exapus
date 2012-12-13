package exapus.gui.views.store;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.collect.Iterables;

import exapus.gui.util.Util;
import exapus.model.DeltaEvent;
import exapus.model.IDeltaListener;
import exapus.model.Observable;
import exapus.model.forest.FactForest;
import exapus.model.forest.PackageTree;
import exapus.model.store.Store;
import exapus.model.view.View;

public class StoreListContentProvider implements IStructuredContentProvider, IDeltaListener {

	protected ListViewer viewer;
	
	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (ListViewer) viewer;
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
	public View[] getElements(Object inputElement) {
		if (inputElement instanceof Store)
			return Iterables.toArray(((Store) inputElement).getRegisteredViews(), View.class);
		else
			return null;
	}
	
	@Override
	public void add(DeltaEvent event) {
		Util.asyncUIThreadIfWidgetNotDisposed(viewer.getControl(), new Runnable() {
			public void run() {
				viewer.refresh();
			}
		});
	}

	@Override
	public void remove(final DeltaEvent event) {
		Util.asyncUIThreadIfWidgetNotDisposed(viewer.getControl(), new Runnable() {
			public void run() {
				viewer.refresh();
			}
		});
	}

}
