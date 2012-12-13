package exapus.gui.views.forest.combined;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import exapus.model.world.World;

public class ForestCombinedViewInput implements IEditorInput {
	
	public ForestCombinedViewInput(String forestName) {
		registeredForestName = forestName;
	}

	private String registeredForestName;
	
	public boolean exists() {
		return World.getCurrent().hasRegisteredForest(registeredForestName);
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return registeredForestName;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return "Forest named:" + getName();
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") final Class adapter) {
		return null;
	}
}