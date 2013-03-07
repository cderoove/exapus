package exapus.gui.views.store;

import org.eclipse.jface.viewers.LabelProvider;
import exapus.model.view.View;

public class StoreListLabelProvider extends LabelProvider {

	public String getText(Object element) {
		View view = (View) element;
		return "<b>" + view.getName() + "</b> <br/>" 
				+ "<em>" + view.getPerspective().getShortLabel() + "-centric</em><br/>"
				+ view.getDescription();
	}

}
