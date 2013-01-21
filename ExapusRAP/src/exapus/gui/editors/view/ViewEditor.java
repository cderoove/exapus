package exapus.gui.editors.view;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.*;
import org.eclipse.ui.part.MultiPageEditorPart;

import exapus.gui.editors.forest.graph.ForestGraphEditor;
import exapus.gui.editors.forest.tree.ForestTreeEditor;
import exapus.gui.editors.view.definition.ViewDefinitionEditor;

public class ViewEditor extends MultiPageEditorPart {

	private ForestTreeEditor forestTree;
	private ForestGraphEditor forestGraph;
	private ViewDefinitionEditor viewDefinition;
	
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
		viewDefinition = new ViewDefinitionEditor();
		int index;
		try {
			index = addPage(viewDefinition, getEditorInput());
			setPageText(index, "Config");
			viewDefinition.setViewEditor(this);
			index = addPage(forestTree, getEditorInput());
			setPageText(index, "Table");
			forestTree.setViewEditor(this);
			index = addPage(forestGraph, getEditorInput());
			setPageText(index, "Graph");
			forestGraph.setViewEditor(this);
		} catch( PartInitException e ) {
			e.printStackTrace();
		}
	}

}
