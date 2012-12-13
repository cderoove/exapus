package exapus.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import exapus.gui.editors.view.ViewEditorInput;
import exapus.gui.editors.view.ViewEditor;
import exapus.gui.views.forest.reference.ForestReferenceViewPart;
import exapus.model.view.Store;

public class ExapusWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ExapusWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer) {
		return new ExapusActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		// configurer.setInitialSize(new Point(800, 600));
		configurer.setShowCoolBar(false);
		configurer.setShowPerspectiveBar(false);
		// configurer.setTitle( "Exapus Workbench" );
		configurer.setShellStyle(SWT.NO_TRIM); // SWT.TITLE | SWT.MAX |
		// SWT.RESIZE );
		configurer.setShowProgressIndicator(true);
		configurer.setShowMenuBar(true);
		configurer.setShowStatusLine(false);

	}

	public void postWindowOpen() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		Shell shell = window.getShell();
		shell.setMaximized(true);

		//TODO
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ViewEditorInput input = new ViewEditorInput("apis");
		try {
			activePage.openEditor(input, ViewEditor.ID, true);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
		Store.getCurrent().populateWorkspaceModel();
		
	}
}
