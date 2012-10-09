package exapus.gui.views.forest;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import exapus.gui.ExapusWorkbench;
import exapus.gui.views.reference.ReferenceViewPart;
import exapus.model.forest.ForestElement;
import exapus.model.forest.Member;
import exapus.model.forest.Ref;

public class FactForestTreeViewPart extends ViewPart implements IDoubleClickListener {

	public static final String ID = "exapus.gui.views.FactForestTreeView";

	public static final String ID_DUAL = "exapus.gui.views.DualFactForestTreeView";

	private TreeViewer viewer;

	public boolean isDualFactForestViewer() {
		return getViewSite().getId().equals(ID_DUAL);
	}

	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.getTree().setData(RWT.MARKUP_ENABLED, Boolean.FALSE); // do not enable
		// html
		// markup ... otherwise I cannot add names of parameterized types 

		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);
		viewer.setUseHashlookup(true);

		/*
		 * int style = SWT.BORDER | SWT.FULL_SELECTION ; Tree tree = new
		 * Tree(parent, style); tree.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		 * tree.setLinesVisible(true); tree.setHeaderVisible(true); viewer = new
		 * TreeViewer(tree);
		 */

		viewer.setContentProvider(new FactForestTreeContentProvider());

		TreeViewerColumn patternCol = new TreeViewerColumn(viewer, SWT.NONE);
		patternCol.getColumn().setText("Pattern");
		patternCol.getColumn().setWidth(350);
		patternCol.setLabelProvider(new FactForestTreeLabelProviders.PatternColumnLabelProvider());

		TreeViewerColumn elementCol = new TreeViewerColumn(viewer, SWT.NONE);
		elementCol.getColumn().setText("Element");
		elementCol.getColumn().setWidth(150);
		elementCol.setLabelProvider(new FactForestTreeLabelProviders.ElementColumnLabelProvider());

		TreeViewerColumn nameCol = new TreeViewerColumn(viewer, SWT.NONE);
		nameCol.getColumn().setText("Name");
		nameCol.getColumn().setWidth(250);
		nameCol.setLabelProvider(new FactForestTreeLabelProviders.NameColumnLabelProvider());

		TreeViewerColumn lineCol = new TreeViewerColumn(viewer, SWT.RIGHT);
		lineCol.getColumn().setText("Line");
		lineCol.getColumn().setWidth(50);
		lineCol.setLabelProvider(new FactForestTreeLabelProviders.LineColumnLabelProvider());


		
		String myID = getViewSite().getId();
		if (myID.equals(ID)) {
			viewer.setInput(ExapusWorkbench.exapusModel.getProjectCentricForest());
		} 
		
		getSite().setSelectionProvider(viewer);
		createSelectionListener();
		
		if (myID.equals(ID_DUAL)) {
			viewer.setInput(ExapusWorkbench.exapusModel.getAPICentricForest());
			
		} 

		// TODO: have a continuously updated reference window open (with code),
		// and an api-centric forest view

		viewer.addDoubleClickListener(this);

	}


	private void createSelectionListener() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		ISelectionService selectionService = window.getSelectionService();
		selectionService.addSelectionListener(new ISelectionListener() {
			public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
				//TODO: add disposal check 
				if (selection != null) {
					IStructuredSelection sselection = (IStructuredSelection) selection;
					Object firstElement = sselection.getFirstElement();
					if (firstElement instanceof Ref) {
						Ref ref = (Ref) firstElement;
						Ref dual = ref.getDual();
						Member dualMember = dual.getParentMember();
						//viewer.reveal(dualMember);
						viewer.setSelection(new StructuredSelection(dualMember), true);
						return;
					}
				}

			}
		});
	}


	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ReferenceViewPart.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

	}

}
