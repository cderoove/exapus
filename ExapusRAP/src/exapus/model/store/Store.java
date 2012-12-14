package exapus.model.store;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import exapus.gui.editors.forest.graph.Graph;
import exapus.gui.editors.forest.graph.GraphViz;
import exapus.model.Observable;
import exapus.model.forest.ExapusModel;
import exapus.model.forest.FactForest;
import exapus.model.view.View;
import exapus.model.view.ViewFactory;
import exapus.model.view.evaluator.Evaluator;

public class Store extends Observable {

	private static Store current;

	static {
		current = new Store();
	}
	
	public static Store getCurrent() {
		return current;
	}
	
	private Store() {
		registry = new HashMap<String, View>();
		viewForests = new HashMap<String, FactForest>();
		viewGraphs = new HashMap<String, Graph>();
		workspaceModel = new ExapusModel();
		registerDefaultViews();
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

	//data
	private Map<String, View> registry;
	
	//caches 
	private Map<String,FactForest> viewForests;
	private Map<String,Graph> viewGraphs;

	
	public ExapusModel getWorkspaceModel() {
		return workspaceModel;
	}
	
	public Iterable<View> getRegisteredViews() {
		return registry.values();
	}
	
	public void registerView(View view) {
		unregisterView(view.getName()); //for caches
		registry.put(view.getName(), view);
	}
	
	public void unregisterView(String name) {
		registry.remove(name);
		viewForests.remove(name);
		viewGraphs.remove(name);
	}
	
	public View getView(String name) {
		return registry.get(name);
	}

	public boolean hasRegisteredView(String name) {
		return registry.containsKey(name);
	}
		
	protected void registerDefaultViews() {
		registerView(ViewFactory.getCurrent().completeAPIView());
		registerView(ViewFactory.getCurrent().completeProjectView());
	}
		
	private boolean hasRegisteredForest(String name) {
		return viewForests.containsKey(name);
	}
	
	private boolean hasRegisteredGraph(String name) {
		return viewGraphs.containsKey(name);
	}
	
	private FactForest evaluateView(String name) {
		return Evaluator.evaluate(getView(name));
	}
	
	private Graph drawView(String name) {
		FactForest forest = forestForRegisteredView(name);
		return null; //TODO
	}
	
	public FactForest forestForRegisteredView(String name, boolean forceEval) {
		if(forceEval)
			viewForests.put(name, evaluateView(name));
		return forestForRegisteredView(name);	
	}
	
	public FactForest forestForRegisteredView(String name) {
		FactForest forest = viewForests.get(name);
		if(forest == null) {
			forest = evaluateView(name);
			viewForests.put(name, forest);
		}
		return forest;
	}
	
	public Graph graphForRegisteredView(String name, boolean forceEval) {
		if(forceEval)
			viewGraphs.put(name, drawView(name));
		return graphForRegisteredView(name);	
	}

	public Graph graphForRegisteredView(String name) {
		Graph graph = viewGraphs.get(name);
		if(graph == null) {
			graph = drawView(name);
			viewGraphs.put(name, graph);
		}
		return graph;
	} 
	
		
	
	
	
	
}
