package exapus.gui.editors.forest.tree;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import exapus.gui.views.forest.reference.ForestReferenceViewPart;
import exapus.model.forest.FactForest;
import exapus.model.store.Store;

public class ForestTreeEditor implements IEditorPart, IDoubleClickListener {

	private IEditorSite editorSite; 
	private IEditorInput editorInput;
	
	public static final String ID = "exapus.gui.views.forest.ForestTreeView";

	/*
	public static int viewerCount = 0;

	private String viewID;

	public String getViewID() {
		return viewID;
	}

	public void setViewID(String secondaryId) {
		this.viewID = secondaryId;
	}
	 */


	//public static final String ID_DUAL = "exapus.gui.views.DualFactForestTreeView";

	private TreeViewer viewer;

	/*
	public boolean isDualFactForestViewer() {
		return getViewSite().getId().equals(ID_DUAL);
	}
	 */

	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.getTree().setData(RWT.MARKUP_ENABLED, Boolean.FALSE); // do not enable html  markup ... otherwise names of parameterized types cannot be added

		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);
		viewer.setUseHashlookup(true);

		/*
		 * int style = SWT.BORDER | SWT.FULL_SELECTION ; Tree tree = new
		 * Tree(parent, style); tree.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		 * tree.setLinesVisible(true); tree.setHeaderVisible(true); viewer = new
		 * TreeViewer(tree);
		 */

		viewer.setContentProvider(new ForestTreeContentProvider());

		TreeViewerColumn patternCol = new TreeViewerColumn(viewer, SWT.NONE);
		patternCol.getColumn().setText("Pattern");
		patternCol.getColumn().setWidth(350);
		patternCol.setLabelProvider(new ForestTreeLabelProviders.PatternColumnLabelProvider());

		TreeViewerColumn elementCol = new TreeViewerColumn(viewer, SWT.NONE);
		elementCol.getColumn().setText("Element");
		elementCol.getColumn().setWidth(150);
		elementCol.setLabelProvider(new ForestTreeLabelProviders.ElementColumnLabelProvider());

		TreeViewerColumn nameCol = new TreeViewerColumn(viewer, SWT.NONE);
		nameCol.getColumn().setText("Name");
		nameCol.getColumn().setWidth(250);
		nameCol.setLabelProvider(new ForestTreeLabelProviders.NameColumnLabelProvider());

		TreeViewerColumn lineCol = new TreeViewerColumn(viewer, SWT.RIGHT);
		lineCol.getColumn().setText("Line");
		lineCol.getColumn().setWidth(50);
		lineCol.setLabelProvider(new ForestTreeLabelProviders.LineColumnLabelProvider());
		
		getSite().setSelectionProvider(viewer);


		/*
		 * Commented out because assumed there were only two, linked forest views.
		 * 
		String myID = getViewSite().getId();
		if (myID.equals(ID)) {
			viewer.setInput(ExapusWorkbench.exapusModel.getProjectCentricForest());
		} 

		getSite().setSelectionProvider(viewer);
		createSelectionListener();

		if (myID.equals(ID_DUAL)) {
			viewer.setInput(ExapusWorkbench.exapusModel.getAPICentricForest());

		} 
		 */
		
		
		/*
		 * TODO: compute view
		String registeredName = editorInput.getName();
		FactForest forest = Store.getCurrent().getForest(registeredName);
		viewer.setInput(forest);
		*/
		
		viewer.addDoubleClickListener(this);


	}


	/*
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

	 */
	
	
	private void update() {
		String viewName = getEditorInput().getName();
		FactForest forest = Store.getCurrent().forestForRegisteredView(viewName,false);
		viewer.setInput(forest);
		viewer.expandToLevel(3);
	}
	
	
	public void setFocus() {
		viewer.getControl().setFocus();
		update();
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ForestReferenceViewPart.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
    protected void setInput(IEditorInput input) {
    	editorInput = input;
	}


	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setInput(input);
		setEditorSite(site);
	}

	private void setEditorSite(IEditorSite site) {
		editorSite = site;
	}


	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {		
	}

	@Override
	public void doSaveAs() {
	}


	@Override
	public void addPropertyListener(IPropertyListener listener) {		
	}


	@Override
	public void dispose() {
	}


	@Override
	public IWorkbenchPartSite getSite() {
		return editorSite;
	}


	@Override
	public String getTitle() {
		if(editorInput == null)
			return "";
		return "Table view on: " + editorInput.getName();
	}


	@Override
	public Image getTitleImage() {
		return null;
	}


	@Override
	public String getTitleToolTip() {
		if(editorInput == null)
			return "";
		return editorInput.getName();
	}

	@Override
	public void removePropertyListener(IPropertyListener listener) {
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}


	@Override
	public boolean isSaveOnCloseNeeded() {
		return false;
	}


	@Override
	public IEditorInput getEditorInput() {
		return editorInput;
	}
	@Override
	public IEditorSite getEditorSite() {
		return editorSite;
	}


}
