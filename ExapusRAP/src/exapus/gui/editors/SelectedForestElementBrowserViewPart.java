package exapus.gui.editors;


import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import exapus.model.forest.ForestElement;

public class SelectedForestElementBrowserViewPart extends ViewPart {

	protected Browser browser;

	@Override
	public void createPartControl(Composite parent) {
		browser = new Browser(parent, SWT.NONE);
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		ISelection selection = window.getSelectionService().getSelection();
		updateBrowser(selection);
		createSelectionListener();
	}


	private void createSelectionListener() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		ISelectionService selectionService = window.getSelectionService();
		selectionService.addSelectionListener(new ISelectionListener() {
			public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
				updateBrowser(selection);
			}
		});
	}

	private void updateBrowser(final ISelection selection) {
		if (browser.isDisposed())
			return;
		if (selection != null) {
			IStructuredSelection sselection = (IStructuredSelection) selection;
			Object firstElement = sselection.getFirstElement();
			if (firstElement instanceof ForestElement) {
				ForestElement fe = (ForestElement) firstElement;
				updateBrowser(fe);
				return;
			}
		}
		browser.setText("");
	}

	
	protected void updateBrowser(ForestElement fe) {
		browser.setText(textToRender(fe));
	}
	
	protected String textToRender(ForestElement fe) {
		return "";
	}
	
	
	@Override
	public void setFocus() {
		browser.setFocus();
	}




}
