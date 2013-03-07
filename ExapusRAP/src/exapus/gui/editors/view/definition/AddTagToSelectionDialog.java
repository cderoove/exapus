package exapus.gui.editors.view.definition;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Shell;

import exapus.model.forest.QName;
import exapus.model.view.Perspective;
import exapus.model.view.Scope;
import exapus.model.view.Selection;

public class AddTagToSelectionDialog extends SelectionDialog {

	public AddTagToSelectionDialog(Shell shell, Perspective perspective, String sourceViewName, Class<? extends Selection> selectionType, QName scopeName, Scope scope) {
		super(shell, perspective, sourceViewName, selectionType, scopeName, scope);
	}
	
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		
		shell.setText("Details for New " + perspective.getShortLabel() + " Tag");

		
	}


}
