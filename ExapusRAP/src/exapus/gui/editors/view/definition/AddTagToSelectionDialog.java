package exapus.gui.editors.view.definition;

import exapus.model.forest.ForestElement;
import exapus.model.view.Perspective;
import exapus.model.view.Scope;
import exapus.model.view.Selection;
import org.eclipse.swt.widgets.Shell;

public class AddTagToSelectionDialog extends SelectionDialog {

    public AddTagToSelectionDialog(Shell shell, Perspective perspective, String sourceViewName,
                                   Class<? extends Selection> selectionType, ForestElement selected, Scope scope) {
        super(shell, perspective, sourceViewName, selectionType, selected, scope);
    }

    protected void configureShell(final Shell shell) {
        super.configureShell(shell);
        shell.setText("Details for New " + perspective.getShortLabel() + " Tag");
    }


}
