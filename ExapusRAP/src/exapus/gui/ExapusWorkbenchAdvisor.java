package exapus.gui;

import org.eclipse.ui.application.*;

public class ExapusWorkbenchAdvisor extends WorkbenchAdvisor {

	public void initialize(IWorkbenchConfigurer configurer) {
		getWorkbenchConfigurer().setSaveAndRestore(false);
		super.initialize(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		return ExplorationPerspective.ID;
		// return "org.eclipse.rap.demo.perspective.planning";
	}

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer windowConfigurer) {
		return new ExapusWorkbenchWindowAdvisor(windowConfigurer);
	}
}
