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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((registeredViewName == null) ? 0 : registeredViewName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		ViewEditorInput other = (ViewEditorInput) obj;
		if(registeredViewName == null)
			return other.registeredViewName == null;
		return registeredViewName.equals(other.registeredViewName);
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
		return "View named: " + getName();
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") final Class adapter) {
		return null;
	}
}