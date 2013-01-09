package exapus.model.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import exapus.model.Observable;
import exapus.model.forest.ExapusModel;
import exapus.model.forest.FactForest;
import exapus.model.view.View;
import exapus.model.view.ViewFactory;

public class Store extends Observable {

	private static Store current;

	static {
        readSettings();
		current = new Store();
	}
	
	public static Store getCurrent() {
		return current;
	}
	
	private Store() {
		registry = new HashMap<String, View>();
		workspaceModel = null;
		registerDefaultViews();
		registerCustomViews();
	}
	
	
	private void initializeModelFromWorkspace(IProgressMonitor m) throws CoreException {
		workspaceModel = new ExapusModel();
		workspaceModel.processWorkspace(m);
	}
	
	public void populateWorkspaceModel() {
		Job job = new Job("Populating fact forests") {
			protected IStatus run(final IProgressMonitor m) {
				try {
					if(workspaceModel == null)
						initializeModelFromWorkspace(m);
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
	
	public ExapusModel getWorkspaceModel() {
		return workspaceModel;
	}
	
	public Iterable<View> getRegisteredViews() {
		return registry.values();
	}
	
	public void registerView(View view) {
		unregisterView(view.getName()); 
		registry.put(view.getName(), view);
		fireUpdate(view);
	}
	
	public void unregisterView(String name) {
		View removed = registry.remove(name);
		if(removed != null)
			fireRemove(removed);
	}
	
	public View getView(String name) {
		return registry.get(name);
	}
	

	public boolean hasRegisteredView(String name) {
		return registry.containsKey(name);
	}
		
	
	protected void registerDefaultViews() {
		registerView(ViewFactory.getCurrent().completePackageView());
		registerView(ViewFactory.getCurrent().completeProjectView());
	}		
	
	protected void registerCustomViews() {
		registerDebugViews();
		registerCSVTagView();
	}

	
	private void registerDebugViews() {
		registerView(ViewFactory.getCurrent().testAPICentricSelectionView()); 
		registerView(ViewFactory.getCurrent().testAPICentricSelectionView2());
		registerView(ViewFactory.getCurrent().testProjectCentricSelectionView());
	}
	
	private void registerCSVTagView() {
		try {
			registerView(ViewFactory.getCurrent().viewFromCSVTags(new File(Settings.API_TAGS.getValue())));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public FactForest forestForRegisteredView(String name) {
		return getView(name).evaluate();
	}
	
	public File graphForRegisteredView(String name) {
		return getView(name).draw();
	}

	// This file should be located in the same dir as eclipse.ini
	// I.e., for Mac OS: PATH_TO_THE_ECLPSE_DIR/Eclipse.app/Contents/MacOS/
	private static final String CONFIG_FILENAME = "config.properties";
	
    private static void readSettings() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(CONFIG_FILENAME));
            Settings.DOT_EXC.setValue(prop.getProperty(Settings.DOT_EXC.key));
            Settings.API_TAGS.setValue(prop.getProperty(Settings.API_TAGS.key));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static enum Settings {
        DOT_EXC("dot.path", "/usr/local/Cellar/graphviz/2.28.0/bin/dot"),
        API_TAGS("tags.path", "/Users/cderoove/Documents/Docs/VUB/research/papers/authored/quaatlas/data/apis.csv");

        private Settings(String key, String defaultValue) {
            this.key = key;
            this.value = defaultValue;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        private String key;
        private String value;
    }

}
