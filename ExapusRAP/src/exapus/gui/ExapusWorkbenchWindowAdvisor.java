package exapus.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import exapus.gui.actions.PopulateExapusModelAction;
import exapus.model.ExapusModel;

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

		if (ExapusWorkbench.exapusModel == null) {
			ExapusWorkbench.exapusModel = new ExapusModel();
			new PopulateExapusModelAction().run();
		}

	}

	public void postWindowOpen() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		Shell shell = window.getShell();
		shell.setMaximized(true);
	}
}
