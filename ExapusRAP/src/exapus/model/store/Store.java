package exapus.model.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
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
import exapus.model.view.ViewReader;
import exapus.model.view.ViewWriter;

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
		
	public void registerViews() {
		registerDefaultViews();
		registerCustomViews();
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
		registerView(ViewFactory.getCurrent().testAPITagSelectionView());
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

	public File fileForRegisteredView(String name) {
		try {
			return getView(name).toFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void registerViewFromFile(File file) throws FileNotFoundException, JAXBException {
		ViewReader reader = new ViewReader();
		View view = reader.read(new FileInputStream(file));
		registerView(view);
	}
		
	// This file should be located in the same dir as eclipse.ini
	// I.e., for Mac OS: PATH_TO_THE_ECLPSE_DIR/Eclipse.app/Contents/MacOS/
	private static final String CONFIG_FILENAME = "exapus.properties";
	
	/*
	private static File getPropertiesFile() {
		return new File(getWorkspaceLocation(), CONFIG_FILENAME);
	}
	
	private static File getWorkspaceLocation() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
	}
	*/
	
    private static void readSettings() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(CONFIG_FILENAME));
            Settings.DOT_EXC.setValue(prop.getProperty(Settings.DOT_EXC.key));
            Settings.API_TAGS.setValue(prop.getProperty(Settings.API_TAGS.key));
            Settings.PROJECT_TEST.setValue(prop.getProperty(Settings.PROJECT_TEST.key));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static enum Settings {
        DOT_EXC("dot.path", "/usr/local/bin/dot"),
        API_TAGS("tags.path", "/Users/cderoove/Documents/Docs/VUB/research/papers/authored/quaatlas/data/apis.csv"),
        PROJECT_TEST("test.project", "sunflow");

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
