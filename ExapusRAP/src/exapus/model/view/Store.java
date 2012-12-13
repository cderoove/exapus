package exapus.model.view;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import exapus.model.forest.ExapusModel;
import exapus.model.forest.FactForest;

public class Store {

	private static Store current;

	static {
		current = new Store();
	}
	
	public static Store getCurrent() {
		return current;
	}
	
	private Store() {
		registry = new HashMap<String, View>();
		workspaceModel = new ExapusModel();
	}
	
	public void populateWorkspaceModel() {
		Job job = new Job("Populating fact forests.") {
			protected IStatus run(final IProgressMonitor m) {
				try {
					workspaceModel.processWorkspace(m);
				} catch (CoreException e) {
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}
	
	private ExapusModel workspaceModel;

	private Map<String, View> registry;
	
	public ExapusModel getWorkspaceModel() {
		return workspaceModel;
	}
	
	public void registerView(String name, View view) {
		registry.put(name, view);
	}
	
	public void unregisterView(String name) {
		registry.remove(name);
	}
	
	public View getView(String name) {
		return registry.get(name);
	}

	public boolean hasRegisteredView(String name) {
		return registry.containsKey(name);
	}
	
	
	
}
