package exapus.gui;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import exapus.gui.actions.PopulateExapusModelAction;
import exapus.model.forest.ExapusModel;

public class ExapusWorkbench implements IEntryPoint {

	/*
	 * private static final String DEMO_PRESENTATION =
	 * "org.eclipse.rap.demo.presentation";
	 */

	public static ExapusModel exapusModel;

	public int createUI() {

		ScopedPreferenceStore prefStore = (ScopedPreferenceStore) PrefUtil.getAPIPreferenceStore();

		String keyPresentationId = IWorkbenchPreferenceConstants.PRESENTATION_FACTORY_ID;
		String presentationId = prefStore.getString(keyPresentationId);

		WorkbenchAdvisor workbenchAdvisor = new ExapusWorkbenchAdvisor();

		/*
		 * if( DEMO_PRESENTATION.equals( presentationId ) ) { worbenchAdvisor =
		 * new DemoPresentationWorkbenchAdvisor(); }
		 */

		Display display = PlatformUI.createDisplay();
		int result = PlatformUI.createAndRunWorkbench(display, workbenchAdvisor);
		display.dispose();
		return result;
	}

}
