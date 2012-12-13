package exapus.gui.editors.view;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import exapus.model.view.Store;

public class ViewEditorInput implements IEditorInput {
	
	public ViewEditorInput(String name) {
		registeredViewName = name;
	}

	private String registeredViewName;
	
	public boolean exists() {
		return Store.getCurrent().hasRegisteredView(registeredViewName);
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return registeredViewName;
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