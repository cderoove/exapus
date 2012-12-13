package exapus.gui.editors.view;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import exapus.gui.editors.forest.graph.ForestGraphEditor;
import exapus.gui.editors.forest.tree.ForestTreeEditor;

public class ViewEditor extends MultiPageEditorPart {

	private ForestTreeEditor forestTree;
	private ForestGraphEditor forestGraph;

	public static final String ID = "exapus.gui.views.forest.ForestCombinedView";

	public ViewEditor() {
		super();
	}

	public void doSave(final IProgressMonitor monitor) {
	}

	public void doSaveAs() {
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		super.init(site, input);
		setPartName(input.getName());
	}

	@Override
	protected void createPages() {
		forestTree = new ForestTreeEditor();
		forestGraph = new ForestGraphEditor();
		int index;
		try {
			index = addPage(forestTree, getEditorInput());
			setPageText(index, "Table");
			index = addPage(forestGraph, getEditorInput());
			setPageText(index, "Graph");
		} catch( PartInitException e ) {
			e.printStackTrace();
		}
	}
}
