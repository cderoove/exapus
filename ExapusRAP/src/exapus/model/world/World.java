package exapus.model.world;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import exapus.model.forest.ExapusModel;
import exapus.model.forest.FactForest;

public class World {

	private static World current;

	static {
		current = new World();
	}
	
	public static World getCurrent() {
		return current;
	}
	
	private World() {
		registry = new HashMap<String, FactForest>();
		workspaceModel = new ExapusModel();
		registerForest("apis", workspaceModel.getAPICentricForest());
		registerForest("projects", workspaceModel.getProjectCentricForest());
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

	private Map<String, FactForest> registry;
	
	public ExapusModel getWorkspaceModel() {
		return workspaceModel;
	}
	
	public void registerForest(String name, FactForest model) {
		registry.put(name, model);
	}
	
	public void unregisterForest(String name) {
		registry.remove(name);
	}
	
	public FactForest getForest(String name) {
		return registry.get(name);
	}

	public boolean hasRegisteredForest(String registeredForest) {
		return registry.containsKey(registeredForest);
	}
	
	
	
}
