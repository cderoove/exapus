package exapus.gui.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

import exapus.gui.ExapusWorkbench;

public class PopulateExapusModelAction extends Action {

	public PopulateExapusModelAction() {
		super();
		setText("Populate forests");
		setId("exapus.gui.actions.populate");

	}

	public void run() {
		Job job = new Job("Populating fact forests.") {
			protected IStatus run(final IProgressMonitor m) {
				try {

					ExapusWorkbench.exapusModel.processWorkspace(m);
				} catch (CoreException e) {
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}
}