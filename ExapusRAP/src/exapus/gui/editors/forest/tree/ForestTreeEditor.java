package exapus.gui.editors.forest.tree;

import exapus.gui.editors.view.IViewEditorPage;
import exapus.gui.editors.view.ViewEditor;
import exapus.gui.views.forest.reference.ForestReferenceViewPart;
import exapus.model.forest.*;
import exapus.model.store.Store;
import exapus.model.view.ProjectCentricView;
import exapus.model.view.View;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.*;

public class ForestTreeEditor implements IEditorPart, IDoubleClickListener, IViewEditorPage {

	private IEditorSite editorSite; 
	private IEditorInput editorInput;
	
	public static final String ID = "exapus.gui.views.forest.ForestTreeView";

    private TreeViewerColumn metricCol;

    private SortBy current = SortBy.NAME;
    private static enum SortBy {
        NAME, METRIC
    }

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
	private ViewEditor viewEditor;

    private MetricComparator comparator;

	/*
	public boolean isDualFactForestViewer() {
		return getViewSite().getId().equals(ID_DUAL);
	}
	 */

    class MetricComparator extends ViewerComparator {
        private int direction = SWT.DOWN;

        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            if (e1.getClass() == e2.getClass() && e1 instanceof ForestElement) {
                ForestElement fe1 = (ForestElement) e1;
                ForestElement fe2 = (ForestElement) e2;

                int result = 0;
                switch (current) {
                    case NAME:
                        result = fe1.getName().toString().compareToIgnoreCase(fe2.getName().toString());
                        break;
                    case METRIC:
                        result = fe1.getMetric().compareTo(fe2.getMetric());
                        break;

                }
                if (direction == SWT.UP) {
                    result = -result;
                }

                return result;
            } else {
                return 0;
            }
        }

        public int change() {
            if (direction == SWT.UP) {
                direction = SWT.DOWN;
            } else {
                direction = SWT.UP;
            }
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }
    }

	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.getTree().setData(RWT.MARKUP_ENABLED, Boolean.FALSE); // do not enable html  markup ... otherwise names of parameterized types cannot be added

		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);
		viewer.setUseHashlookup(true);

        comparator = new MetricComparator();
        viewer.setComparator(comparator);

		/*
		 * int style = SWT.BORDER | SWT.FULL_SELECTION ; Tree tree = new
		 * Tree(parent, style); tree.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		 * tree.setLinesVisible(true); tree.setHeaderVisible(true); viewer = new
		 * TreeViewer(tree);
		 */

		viewer.setContentProvider(new ForestTreeContentProvider());

		final TreeViewerColumn patternCol = new TreeViewerColumn(viewer, SWT.NONE);
		patternCol.getColumn().setText("Pattern");
		patternCol.getColumn().setWidth(350);
		patternCol.setLabelProvider(new ForestTreeLabelProviders.PatternColumnLabelProvider());

        patternCol.getColumn().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                viewer.getTree().setSortColumn(patternCol.getColumn());
                if (current == SortBy.NAME) {
                    viewer.getTree().setSortDirection(comparator.change());
                } else {
                    current = SortBy.NAME;
                    comparator.setDirection(SWT.DOWN);
                }
                TreePath[] expanded = viewer.getExpandedTreePaths();
                viewer.refresh();
                viewer.setExpandedTreePaths(expanded);
            }
        });

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

        metricCol = new TreeViewerColumn(viewer, SWT.RIGHT);
        metricCol.getColumn().setText("#");
        metricCol.getColumn().setWidth(100);
        metricCol.setLabelProvider(new ForestTreeLabelProviders.MetricColumnLabelProvider());

        metricCol.getColumn().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                viewer.getTree().setSortColumn(metricCol.getColumn());
                if (current == SortBy.METRIC) {
                    viewer.getTree().setSortDirection(comparator.change());
                } else {
                    current = SortBy.METRIC;
                    comparator.setDirection(SWT.DOWN);
                }
                TreePath[] expanded = viewer.getExpandedTreePaths();
                viewer.refresh();
                viewer.setExpandedTreePaths(expanded);
            }
        });

/*
        // For debugging purposes
        TreeViewerColumn debug = new TreeViewerColumn(viewer, SWT.RIGHT);
        debug.getColumn().setText("Debug");
        debug.getColumn().setWidth(150);
        debug.setLabelProvider(new ForestTreeLabelProviders.DebugColumnLabelProvider());
*/

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


		Action expandAllAction = new Action() {
			@Override
			public void run() {
				viewer.expandAll();
			}			
		};
		expandAllAction.setText("Expand all packages");
		expandAllAction.setId("exapus.gui.editors.forest.tree.ExpandAllAction");
		expandAllAction.setImageDescriptor(getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));
		registerAction(expandAllAction);

		
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
	
	
	public void updateControls() {
        metricCol.getColumn().setText(viewEditor.getCurrentMetric().getShortName());

		String viewName = getEditorInput().getName();
		FactForest forest = Store.getCurrent().forestForRegisteredView(viewName);
		viewer.setInput(forest);
        if (getView() instanceof ProjectCentricView) {
            viewer.expandToLevel(4);
        } else {
            viewer.expandToLevel(3);
        }
	}

    private View getView() {
        return Store.getCurrent().getView(getEditorInput().getName());

    }
	
	public void setFocus() {
		viewer.getControl().setFocus();
		updateControls();
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

	public void setViewEditor(ViewEditor viewEditor) {
		this.viewEditor = viewEditor;
	}
	
	
	private void registerAction(Action action) {
		getEditorSite().getActionBars().getToolBarManager().add(action);  
	}

	private IWorkbench getWorkBench() {
		return getSite().getWorkbenchWindow().getWorkbench();
	}
	
	private ImageDescriptor getImageDescriptor(String name) {
		return getWorkBench().getSharedImages().getImageDescriptor(name);
	}



}
