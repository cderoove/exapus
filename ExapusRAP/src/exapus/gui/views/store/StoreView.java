package exapus.gui.views.store;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import exapus.gui.editors.view.ViewEditor;
import exapus.gui.editors.view.ViewEditorInput;
import exapus.gui.views.forest.reference.ForestReferenceViewPart;
import exapus.model.view.Store;
import exapus.model.view.View;

public class StoreView extends ViewPart implements IDoubleClickListener {
	
	public static final String ID = "exapus.gui.views.store.StoreView";

	private ListViewer listView;
	
	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());
		listView = new ListViewer(parent);
		listView.setContentProvider(new StoreListContentProvider());
		listView.addDoubleClickListener(this);
		listView.setInput(Store.getCurrent());	
	}

	@Override
	public void setFocus() {
		listView.getControl().setFocus();
	}
	
	
	private void openViewEditorOn(View v) {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ViewEditorInput input = new ViewEditorInput(v.getName());
		try {
			activePage.openEditor(input, ViewEditor.ID, true);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		if(selection.isEmpty())
			return;
		Object selected = selection.getFirstElement();
		if(selected instanceof View) {
			openViewEditorOn((View) selected);
		}
	}


}
