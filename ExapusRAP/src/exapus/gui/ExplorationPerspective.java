package exapus.gui;

import org.eclipse.ui.*;

import exapus.gui.views.forest.FactForestTreeViewPart;
import exapus.gui.views.reference.ReferenceViewPart;

public class ExplorationPerspective implements IPerspectiveFactory {

	public static final String ID = "exapus.gui.perspective.exploration";

	@Override
	public void createInitialLayout(final IPageLayout layout) {
		// TODO: You can also add placeholders for views via the
		// layout.addPlaceholder() method call. This methods accepts wildcards
		// and a View with a matching ID would open in this area. For example if
		// you want to open all views in a specific place you could use the
		// layout.addPlaceholder("*",...) method call, or
		// layout.addPlaceholder("view_id",....) to open a View with the
		// "view_id" ID in this placeholder.

		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		// TODO: list available view specifications
		IFolderLayout specificationsFolder = layout.createFolder("specifications", IPageLayout.TOP, 075f, editorArea);
		specificationsFolder.addView("org.eclipse.rap.demo.DemoTreeViewPartI"); // project-centric
																				// or
																				// api-centric

		// layout.addStandaloneView("org.eclipse.rap.demo.DemoTreeViewPartI",
		// false, IPageLayout.TOP, 075f, editorArea);

		IFolderLayout resultsFolder = layout.createFolder("results", IPageLayout.RIGHT, 0.20f, "specifications");
		resultsFolder.addView(FactForestTreeViewPart.ID); // TODO: meerdere
															// laten openen

		layout.addStandaloneView(FactForestTreeViewPart.ID_DUAL, true, IPageLayout.RIGHT, 0.80f, "results");
		
		layout.addStandaloneView(ReferenceViewPart.ID, true, IPageLayout.BOTTOM, 0.60f, "results");

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
