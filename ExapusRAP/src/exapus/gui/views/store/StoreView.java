package exapus.gui.views.store;

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.part.ViewPart;

import exapus.model.view.Store;

public class StoreView extends ViewPart {
	
	public static final String ID = "exapus.gui.views.store.StoreView";

	private ListViewer listView;
	
	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());
		listView = new ListViewer(parent);
		listView.setContentProvider(new StoreListContentProvider());
		listView.setInput(Store.getCurrent());
	}

	@Override
	public void setFocus() {
		listView.getControl().setFocus();
	}
	


}
