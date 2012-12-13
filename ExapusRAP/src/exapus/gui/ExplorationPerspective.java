package exapus.gui;

import org.eclipse.ui.*;

import exapus.gui.views.forest.graph.ForestGraphViewPart;
import exapus.gui.views.forest.reference.ForestReferenceViewPart;
import exapus.gui.views.forest.tree.ForestTreeViewPart;

public class ExplorationPerspective implements IPerspectiveFactory {

	public static final String ID = "exapus.gui.perspective.exploration";

	@Override
	public void createInitialLayout(final IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true); 

		IFolderLayout specificationsFolder = layout.createFolder("specifications", IPageLayout.LEFT, 0.25f, editorArea);
		specificationsFolder.addView("org.eclipse.rap.demo.DemoTreeViewPartI"); // project-centric
																				// or
																				// api-centric

		
		layout.addStandaloneView(ForestReferenceViewPart.ID, true, IPageLayout.BOTTOM, 0.60f, editorArea);
		
		
		
		//layout.addStandaloneView("org.eclipse.ui.views.properties.PropertySheet", true, IPageLayout.BOTTOM, 0.60f, "results");
		

		// IFolderLayout topRight = layout.createFolder("topRight",
		// IPageLayout.RIGHT, 0.70f, editorArea);
		// topRight.addView("org.eclipse.rap.demo.DemoSelectionViewPart");

		// IFolderLayout middleRight = layout.createFolder("middleRight",
		// IPageLayout.BOTTOM, 0.70f, topRight.);
		// middleRight.addView("org.eclipse.rap.demo.DemoSelectionViewPart");

		// add shortcuts to show view menu
		// layout.addShowViewShortcut("org.eclipse.rap.demo.DemoTreeViewPartI");
		// layout.addShowViewShortcut("org.eclipse.rap.demo.DemoTreeViewPartII");

		// add shortcut for other perspective
		// layout.addPerspectiveShortcut(
		// "org.eclipse.rap.demo.perspective.planning" );
	}
}
