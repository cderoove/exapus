package exapus.gui.views.store;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import exapus.gui.editors.view.ViewEditor;
import exapus.gui.editors.view.ViewEditorInput;
import exapus.model.store.Store;
import exapus.model.view.Perspective;
import exapus.model.view.View;

public class StoreView extends ViewPart implements IDoubleClickListener {
	
	public static final String ID = "exapus.gui.views.store.StoreView";

	private ListViewer listView;
	
	private String promptForUniqueViewName(String dialogTitle) {
		IInputValidator viewNameValidator = new IInputValidator() {
			@Override
			public String isValid(String newText) {
				if(newText.trim().length() < 1)
					return "Name is too short!";
				if(Store.getCurrent().hasRegisteredView(newText))
					return "Name is not unique!";
				return null;
			}
		};
		InputDialog dlg = new InputDialog(getSite().getShell(), dialogTitle, "Please enter a name for the view.", "New view", viewNameValidator);
		if(dlg.open() == Window.OK) 
			return dlg.getValue();
		else
			return null;
	}
	
	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());
		listView = new ListViewer(parent);
		listView.setContentProvider(new StoreListContentProvider());
		listView.addDoubleClickListener(this);
		listView.setInput(Store.getCurrent());	
		
		Action newViewAction = new Action() {
			@Override
			public void run() {
				String name = promptForUniqueViewName("Create new view");
				if(name != null) 
					Store.getCurrent().registerView(new View(name,Perspective.PROJECT_CENTRIC));
					
			}			
		};
		newViewAction.setText("Create new view");
		newViewAction.setId("exapus.gui.views.store.actions.NewViewAction");
		newViewAction.setImageDescriptor(getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
		registerAction(newViewAction);
		
		final Action duplicateViewAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) listView.getSelection();
				if(selection.isEmpty())
					return;
				Object selected = selection.getFirstElement();
				if(selected instanceof View) {
					View selectedView = (View) selected;
					String name = promptForUniqueViewName("Duplicate existing view");
					if(name != null) {
						View duplicatedView = View.fromView(selectedView);
						duplicatedView.setName(name);
						Store.getCurrent().registerView(duplicatedView);
					}
				}
			}						
		};
		duplicateViewAction.setText("Duplicate selected view");
		duplicateViewAction.setId("exapus.gui.views.store.actions.DuplicateViewAction");
		duplicateViewAction.setImageDescriptor(getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		duplicateViewAction.setEnabled(false);
		registerAction(duplicateViewAction);
		listView.addSelectionChangedListener(new ISelectionChangedListener() {			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				duplicateViewAction.setEnabled(!event.getSelection().isEmpty());
			}
		});
		
		final Action deleteViewAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) listView.getSelection();
				if(selection.isEmpty())
					return;
				Object selected = selection.getFirstElement();
				if(selected instanceof View) {
					String name = ((View) selected).getName();
					Store.getCurrent().unregisterView(name);
					IWorkbenchPage wbPage = getSite().getPage();
					IEditorPart openedEditor = wbPage.findEditor(new ViewEditorInput(name));
					if(openedEditor != null)
						wbPage.closeEditor(openedEditor, false);
				}
			}						
		};
		deleteViewAction.setText("Delete selected view");
		deleteViewAction.setId("exapus.gui.views.store.actions.DeleteViewAction");
		deleteViewAction.setImageDescriptor(getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		deleteViewAction.setEnabled(false);
		registerAction(deleteViewAction);
		listView.addSelectionChangedListener(new ISelectionChangedListener() {			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				deleteViewAction.setEnabled(!event.getSelection().isEmpty());
			}
		});
		

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
	
	private void registerAction(Action action) {
		getViewSite().getActionBars().getToolBarManager().add(action);  
	}

	private IWorkbench getWorkBench() {
		return getSite().getWorkbenchWindow().getWorkbench();
	}
	
	private ImageDescriptor getImageDescriptor(String name) {
		return getWorkBench().getSharedImages().getImageDescriptor(name);
	}


}
