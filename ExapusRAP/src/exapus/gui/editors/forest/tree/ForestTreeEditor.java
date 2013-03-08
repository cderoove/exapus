package exapus.gui.editors.forest.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;

import exapus.gui.editors.view.IViewEditorPage;
import exapus.gui.editors.view.ViewEditor;
import exapus.gui.editors.view.definition.AddTagToSelectionDialog;
import exapus.gui.util.Util;
import exapus.gui.views.forest.reference.ForestReferenceViewPart;
import exapus.gui.views.forest.tagcloud.ForestElementTagCloudViewPart;
import exapus.gui.views.store.StoreView;
import exapus.model.forest.FactForest;
import exapus.model.forest.ForestElement;
import exapus.model.forest.Ref;
import exapus.model.metrics.MetricType;
import exapus.model.store.Store;
import exapus.model.view.Perspective;
import exapus.model.view.Scope;
import exapus.model.view.ScopedSelection;
import exapus.model.view.Selection;
import exapus.model.view.View;

public class ForestTreeEditor implements IEditorPart, IDoubleClickListener, IViewEditorPage {

	private IEditorSite editorSite;
	private IEditorInput editorInput;

	public static final String ID = "exapus.gui.views.forest.ForestTreeView";

	private List<MetricColumn> metricCols = new ArrayList<MetricColumn>();
	private MetricType chosen;
	private int idxFirstMetricCol;

	private TreePath[] revealedPaths;

	private TreeViewerColumn patternCol;
	private Combo comboGroupingPackages;

	private SortBy sorting = SortBy.NAME;
	private MetricType sortingMetric;

	private ForestTreeNameFilterVisitor filter;

	private static enum SortBy {
		NAME, METRIC
	}

	private PackageStyle packageStyle = PackageStyle.FLAT;

	private static enum PackageStyle {
		FLAT, HIERARCHICAL
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
	private ToolItem hierarchical;
	private ToolItem flat;
	private Text apiFilterText;
	private Text projectFilterText;
	private ToolItem filterButton;
	private Composite filterComposite;

	private Composite editorComposite;
	private ToolItem revealButton;
	private TreeViewerColumn tagsCol;
	private TreeViewerColumn duallTagsCol;
	private ToolItem annotateButton;

	/*
     public boolean isDualFactForestViewer() {
         return getViewSite().getId().equals(ID_DUAL);
     }
	 */

	class MetricComparator extends ViewerComparator {
		private int direction = SWT.UP;

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if (e1.getClass() == e2.getClass() && e1 instanceof ForestElement) {
				ForestElement fe1 = (ForestElement) e1;
				ForestElement fe2 = (ForestElement) e2;

				int result = 0;
				switch (sorting) {
				case NAME:
					switch (packageStyle) {
					case HIERARCHICAL:
						result = fe1.getName().toString().compareToIgnoreCase(fe2.getName().toString());
						break;
					case FLAT:
						result = fe1.getQName().toString().compareToIgnoreCase(fe2.getQName().toString());
						break;
					}
					break;
				case METRIC:
					result = fe1.getMetric(sortingMetric).compareTo(fe2.getMetric(sortingMetric), packageStyle == PackageStyle.FLAT);
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


		editorComposite = parent;

		parent.setLayout(new GridLayout(1,false));

		// Toolbar
		ToolBarManager tbMgr = new ToolBarManager(SWT.NONE);
		ToolBar bar = tbMgr.createControl(parent);

		bar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false,1,1)); 

		hierarchical = new ToolItem(bar, SWT.RADIO);
		hierarchical.setToolTipText("Hierarchical package presentation");
		hierarchical.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( final SelectionEvent event ) {
				updatePackageStyle(PackageStyle.HIERARCHICAL);
			}
		});
		hierarchical.setImage(Util.getImageFromPlugin("hierarchicalLayout.gif"));
		flat = new ToolItem(bar, SWT.RADIO);
		flat.setToolTipText("Flat package presentation");
		flat.setImage(Util.getImageFromPlugin("flatLayout.gif"));
		flat.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( final SelectionEvent event ) {
				updatePackageStyle(PackageStyle.FLAT);
			}
		});
		new ToolItem(bar, SWT.SEPARATOR);



		//buttons
		ToolItem expandButton = new ToolItem(bar, SWT.PUSH);
		expandButton.setToolTipText("Expand top levels");
		expandButton.setImage(Util.getImageFromPlugin("expandall.gif"));
		expandButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( final SelectionEvent event ) {
				viewer.expandToLevel(2);
			}
		});

		/*
		 * Expanding particular items does not seem to work

	    ToolItem expandLayersButton = new ToolItem(bar, SWT.PUSH);
	    expandLayersButton.setToolTipText("Expand Package Layers");
	    //expandButton.setImage(Util.getImageFromPlugin("expandall.gif"));
	    expandLayersButton.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected( final SelectionEvent event ) {
	    		for(ForestElement e : getForest().getAllLayers())
	    			viewer.setExpandedState(e, true);	
	    }
	    });
		 */


		ToolItem collapseButton = new ToolItem(bar, SWT.PUSH);
		collapseButton.setToolTipText("Collapse All");
		collapseButton.setImage(Util.getImageFromPlugin("collapseall.gif"));
		collapseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( final SelectionEvent event ) {
				viewer.collapseAll();
			}
		});

		new ToolItem(bar, SWT.SEPARATOR);

		filterButton = new ToolItem(bar, SWT.CHECK);
		filterButton.setToolTipText("Filter References");
		filterButton.setImage(Util.getImageFromPlugin("filter_Action.gif"));
		filterButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				boolean tobefiltered = filterButton.getSelection();
				updateFilterControls(tobefiltered);
				applyFilter(tobefiltered);
			}

		});
		filterButton.setSelection(false);


		new ToolItem(bar, SWT.SEPARATOR);	    
		
		revealButton = new ToolItem(bar, SWT.PUSH);
		revealButton.setToolTipText("Reveal selection in other view");
		revealButton.setImage(Util.getImageFromPlugin("link-editor.gif"));
		revealButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				revealSelectionInOtherView();
			}

		});
		
		annotateButton = new ToolItem(bar, SWT.PUSH);
		annotateButton.setToolTipText("Add " + getView().getPerspective().getShortLabel() + " tag to selection");
		annotateButton.setImage(Util.getImageFromPlugin("annotate.gif"));
		annotateButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				addTagToSelection();
			}

		});


		// Tree viewer

		/*
        PatternFilter filter = new PatternFilter() {
        	@Override
        	protected boolean isLeafMatch(final Viewer viewer, final Object element) {
        		TreeViewer treeViewer = (TreeViewer) viewer;
        		ForestElement fe = (ForestElement) element;  
        		if(fe instanceof Ref) {
        			Ref ref = (Ref) fe;
        			return wordMatches(ref.getReferencedName().toString())
        					|| wordMatches(ref.getReferencingName().toString());
        		}
        		return wordMatches(fe.getQName().toString());
        	}
        };

        FilteredTree filtered = new FilteredTree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION, filter, true);
        viewer = filtered.getViewer();
		 */

		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,1,1)); 


		viewer.getTree().setData(RWT.MARKUP_ENABLED, Boolean.FALSE); // do not enable html  markup ... otherwise names of parameterized types cannot be added


		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);
		viewer.setUseHashlookup(true);



		comparator = new MetricComparator();
		viewer.setComparator(comparator);

		viewer.setContentProvider(packageStyle == PackageStyle.FLAT ? new ForestTreeGroupedPackagesContentProvider() : new ForestTreeContentProvider());

		patternCol = new TreeViewerColumn(viewer, SWT.NONE);
		patternCol.getColumn().setText("Pattern");
		patternCol.getColumn().setWidth(350);
		patternCol.setLabelProvider(new ForestTreeLabelProviders.PatternColumnLabelProvider(packageStyle == PackageStyle.FLAT));

		patternCol.getColumn().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.getTree().setSortColumn(patternCol.getColumn());
				if (sorting == SortBy.NAME) {
					viewer.getTree().setSortDirection(comparator.change());
				} else {
					sorting = SortBy.NAME;
					comparator.setDirection(SWT.DOWN);
				}
				TreePath[] expanded = viewer.getExpandedTreePaths();
				viewer.refresh();
				viewer.setExpandedTreePaths(expanded);
			}
		});

		tagsCol = new TreeViewerColumn(viewer, SWT.NONE);
		tagsCol.getColumn().setText("Tags");
		tagsCol.getColumn().setWidth(150);
		tagsCol.setLabelProvider(new ForestTreeLabelProviders.TagsColumnLabelProvider());
		
		duallTagsCol = new TreeViewerColumn(viewer, SWT.NONE);
		duallTagsCol.getColumn().setText("Dual Tags");
		duallTagsCol.getColumn().setWidth(150);
		duallTagsCol.setLabelProvider(new ForestTreeLabelProviders.DualTagsColumnLabelProvider());

		
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

		chosen = getView().getMetricType();
		idxFirstMetricCol = viewer.getTree().getColumnCount();
		for (MetricType metric : MetricType.supportedMetrics(getView().getRenderable())) {
			if (metric == MetricType.ALL) continue;
			MetricColumn col = new MetricColumn(viewer.getTree().getColumnCount(), metric, viewer, SWT.RIGHT);
			metricCols.add(col);
		}

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

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateSelectionDependentButtons();
			}
		});



		filterComposite = new Composite(parent, SWT.NONE);
		filterComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		filterComposite.setLayout(new GridLayout(2,false));
		
		Label apiFilterLabel = new Label(filterComposite, SWT.NONE);
		apiFilterLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,1,1));
		apiFilterLabel.setText("Referenced Name:");
		apiFilterText = new Text(filterComposite, SWT.BORDER);
		apiFilterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,1,1));
		apiFilterText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				filter.setApiFilter(apiFilterText.getText());
				applyFilter(true);
			}
		});
		
		/*
		Label apiFilterLabel = new Label(filterComposite, SWT.NONE);
		apiFilterLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,1,1));
		apiFilterLabel.setText("Referenced Name:");
		apiFilterText = new Text(filterComposite, SWT.BORDER);
		apiFilterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,1,1));


		Label projectFilterLabel = new Label(filterComposite, SWT.NONE);
		projectFilterLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,1,1));
		projectFilterLabel.setText("Referencing Name:");
		projectFilterText = new Text(filterComposite, SWT.BORDER);
		projectFilterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,1,1));		
		projectFilterText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				filter.setProjectFilter(projectFilterText.getText());
				applyFilter(true);
			}
		});

		*/
		//cannot make the viewer fill the freed space
		//filterComposite.setVisible(filterButton.getSelection());
		filterComposite.setEnabled(filterButton.getSelection());

	}

	protected void addTagToSelection() {
		ForestElement selected = getSelectedForestElement();
		if(selected == null)
			return;
		View targetView = getView(); 
		Perspective perspective = targetView.getPerspective(); 
		AddTagToSelectionDialog selectionDialog  = new AddTagToSelectionDialog(getSite().getShell(),
				perspective, 
				targetView.getName(),
				ScopedSelection.class,
				selected,
				Scope.forTagging(selected));		
		int returnCode = selectionDialog.open();
		if(returnCode == IDialogConstants.OK_ID) {
			Selection newSelection = selectionDialog.getSelection();
			if(newSelection != null) {
				if(perspective.equals(Perspective.API_CENTRIC))
					targetView.addAPISelection(newSelection);
				if(perspective.equals(Perspective.PROJECT_CENTRIC))
					targetView.addProjectSelection(newSelection);
			}
		}
		updateControls();
	
	}

		
	


	public void setFilter(String projectFilter, String apiFilter) {
		projectFilterText.setText(projectFilter);
		apiFilterText.setText(apiFilter);
		filter.setProjectFilter(projectFilter);
		filter.setApiFilter(apiFilter);
		updateFilterControls(true);
		applyFilter(true);
	}
	
	
	private ForestElement getSelectedForestElement() {
		StructuredSelection sel = (StructuredSelection) viewer.getSelection();
		if(sel.isEmpty())
			return null;
		ForestElement selected = (ForestElement) sel.getFirstElement();
		return selected;
	}

	private void revealSelectionInOtherView() {
		ForestElement selected = getSelectedForestElement();
		if(selected == null)
			return;
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getSite().getShell(), new LabelProvider());
		dialog.setElements(Iterables.toArray(Store.getCurrent().getRegisteredViews(), Object.class));
		dialog.setTitle("Choose View");
		dialog.setMultipleSelection(false);
		dialog.setMessage("The selected element will be revealed in the chosen view.");

		//dialog.setValidator(validator);

		if(dialog.open() == Window.OK) {
			Object[] selection = dialog.getResult(); 
			View selectedView = (View) selection[0];
			revealForestElementInView(selected, selectedView);
		}


	}


	private void revealForestElementInView(ForestElement selected, View selectedView) {
		String errorTitle = "Error revealing selected element";
		Shell shell = getSite().getShell();
		IEditorPart openedPart = StoreView.openViewEditorOn(selectedView);
		if(!(openedPart instanceof ViewEditor)) {
			MessageDialog.openError(shell, errorTitle, "Could not open chosen view.");
			return;
		}
		ViewEditor opened = (ViewEditor) openedPart;
		opened.activateForestTreeEditor();
		ForestTreeEditor openedTree = opened.getForestTreeEditor();
		FactForest openedForest = openedTree.getForest();
		ForestElement correspondingElement = null;
		if(selectedView.getPerspective().equals(getView().getPerspective()))				
			correspondingElement = openedForest.getCorrespondingForestElement(selected);
		else {

			if(selected instanceof Ref) {
				Ref dualRef = ((Ref) selected).getDual();
				if(dualRef != null) 
					correspondingElement = openedForest.getCorrespondingForestElement(dualRef);
			} else {
				//MessageDialog.openError(shell, errorTitle, "Only references can be revealed in a view from the other perspective.");
				if(selectedView.isAPICentric()) 
					openedTree.setFilter(selected.getQName().toString(), "");
				else
					openedTree.setFilter("",selected.getQName().toString());
				return;

				/*
				//following is rather slow for more than a couple of selections (e.g., 10)
				//disabled because only selecting 10 probably confuses the users
				if(selected instanceof Member) {
					FactForest filteredOpenedForest = (FactForest) openedTree.viewer.getInput();
					Member selectedMember = (Member) selected;
					ArrayList<ForestElement> correspondingDualMemberRefs = new ArrayList<ForestElement>();
					int count = 0;
					for(Ref ref : selectedMember.getReferences()) {
						Ref dual = ref.getDual();
						if(dual != null) {
							ForestElement correspondingDual = filteredOpenedForest.getCorrespondingForestElement(dual); 
							if(correspondingDual != null) {
								correspondingDualMemberRefs.add(correspondingDual);
								count = count + 1;
							}
							if(count > 10)
								break;
						}

						openedTree.viewer.setSelection(new StructuredSelection(correspondingDualMemberRefs), true);
						return;
					}
				}
				*/
			}
		}

		if(correspondingElement != null) 
			openedTree.viewer.setSelection(new StructuredSelection(correspondingElement), true);
		else
			MessageDialog.openError(shell, errorTitle, "Could not find corresponding element in chosen view.");

	}


			private void updateFilterControls(boolean tobefiltered) {
				//cannot make the viewer fill the freed space
				//filterComposite.setVisible(tobefiltered);
				filterButton.setSelection(tobefiltered);
				filterComposite.setEnabled(tobefiltered);
			}


			private void applyFilter(boolean tobefiltered) {
				Object[] expanded = viewer.getExpandedElements();
				StructuredSelection selected = (StructuredSelection) viewer.getSelection();

				FactForest newForest;
				if(tobefiltered)
					newForest = filter.copy(getForest());
				else
					newForest = getForest();

				viewer.setInput(newForest);

				Object[] newExpanded = newForest.getCorrespondingForestElements(expanded);
				StructuredSelection newSelected = new StructuredSelection(newForest.getCorrespondingForestElements(selected.toArray()));

				viewer.setSelection(newSelected, true);
				viewer.setExpandedElements(newExpanded);    


			}


			private void updatePackageStyle(PackageStyle selected) {
				if (selected == packageStyle) return;

				packageStyle = selected;

				switch (selected) {
				case HIERARCHICAL:
					viewer.setContentProvider(new ForestTreeContentProvider());
					break;
				case FLAT:
					viewer.setContentProvider(new ForestTreeGroupedPackagesContentProvider());
					break;
				}

				patternCol.setLabelProvider(new ForestTreeLabelProviders.PatternColumnLabelProvider(packageStyle == PackageStyle.FLAT));
				for (MetricColumn metricCol : metricCols) {
					metricCol.column.setLabelProvider(new ForestTreeLabelProviders.MetricColumnLabelProvider(packageStyle == PackageStyle.FLAT, metricCol.metricType));
				}


				//setting tree paths do not seem to have an effect?
				TreePath[] expanded = viewer.getExpandedTreePaths();
				ISelection selection = viewer.getSelection();
				viewer.refresh();
				viewer.setExpandedTreePaths(expanded);
				viewer.setSelection(selection, true);
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



			private FactForest getForest() {
				String viewName = getEditorInput().getName();
				return Store.getCurrent().forestForRegisteredView(viewName);
			}



			private void updatePackageStyleButtons() {
				hierarchical.setSelection(packageStyle == PackageStyle.HIERARCHICAL);
				flat.setSelection(packageStyle == PackageStyle.FLAT);
			}

			public void updateControls() {
				updateMetrics();
				FactForest newForest = getForest();
				if(viewer.getInput() != newForest) {
					Object[] oldExpanded = viewer.getExpandedElements();
					Object[] newExpanded = newForest.getCorrespondingForestElements(oldExpanded);
					StructuredSelection oldSelected = (StructuredSelection) viewer.getSelection();
					StructuredSelection newSelected = new StructuredSelection(newForest.getCorrespondingForestElements(oldSelected.toArray()));
					
					viewer.setInput(getForest());
					viewer.setSelection(newSelected, true);
					viewer.setExpandedElements(newExpanded);    
					
				}
				updatePackageStyleButtons();
				updateSelectionDependentButtons();
				updateTagColumnLabels();
			}

			
			private void updateTagColumnLabels() {
				Perspective p = getView().getPerspective();
				tagsCol.getColumn().setText(p.getShortLabel() + " Tags");
				duallTagsCol.getColumn().setText(p.getDual().getShortLabel() + " Tags");	
			}



			private void updateSelectionDependentButtons() {
				boolean hasSelection = !viewer.getSelection().isEmpty();
				revealButton.setEnabled(hasSelection);
				annotateButton.setEnabled(hasSelection);
			}



			private void updateMetrics() {
				chosen = getView().getMetricType();
				sorting = SortBy.NAME;
				sortingMetric = chosen;
				updatePackageStyle(PackageStyle.HIERARCHICAL);

				List<Integer> idx = new ArrayList<Integer>();
				for (int i = 0; i < viewer.getTree().getColumnCount(); i++) {
					idx.add(i);
				}

				if (getView().getMetricType() != MetricType.ALL) {
					for (MetricColumn metricCol : metricCols) {
						if (metricCol.metricType != getView().getMetricType()) metricCol.clear();
						else {
							int swapIdx = idx.get(idxFirstMetricCol);
							idx.set(idxFirstMetricCol, metricCol.idx);
							idx.set(metricCol.idx, swapIdx);

							metricCol.init();
						}
					}

				} else {
					for (MetricColumn metricCol : metricCols) {
						metricCol.init();
					}
				}

				viewer.getTree().setColumnOrder(Ints.toArray(idx));
			}

			private View getView() {
				return Store.getCurrent().getView(getEditorInput().getName());

			}

			public void setFocus() {
				updateControls();
				viewer.getControl().setFocus();
			}

			@Override
			public void doubleClick(DoubleClickEvent event) {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ForestReferenceViewPart.ID);
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ForestElementTagCloudViewPart.ID);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}

			protected void setInput(IEditorInput input) {
				editorInput = input;
			}


			private void initFilter() {
				filter = new ForestTreeNameFilterVisitor();
			}

			@Override
			public void init(IEditorSite site, IEditorInput input) throws PartInitException {
				setInput(input);
				setEditorSite(site);
				initFilter();
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
				if (editorInput == null)
					return "";
				return "Table view on: " + editorInput.getName();
			}


			@Override
			public Image getTitleImage() {
				return null;
			}


			@Override
			public String getTitleToolTip() {
				if (editorInput == null)
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

			private IWorkbench getWorkBench() {
				return getSite().getWorkbenchWindow().getWorkbench();
			}

			private void registerAction(Action action) {
				getEditorSite().getActionBars().getToolBarManager().add(action);
			}

			private class MetricColumn {
				private int idx;
				private MetricType metricType;
				private TreeViewerColumn column;
				private SelectionAdapter selectionAdapter;

				public MetricColumn(int idx, MetricType metricType, TreeViewer viewer, int style) {
					this.idx = idx;
					this.metricType = metricType;
					this.column = new TreeViewerColumn(viewer, style);
					init();
				}

				private void init() {
					if (selectionAdapter == null) {
						selectionAdapter = new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								viewer.getTree().setSortColumn(column.getColumn());
								sortingMetric = metricType;

								updatePackageStyle(PackageStyle.FLAT);
								updatePackageStyleButtons();

								if (sorting == SortBy.METRIC) {
									viewer.getTree().setSortDirection(comparator.change());
								} else {
									sorting = SortBy.METRIC;
									comparator.setDirection(SWT.UP);
								}
								TreePath[] expanded = viewer.getExpandedTreePaths();
								viewer.refresh();
								viewer.setExpandedTreePaths(expanded);
							}
						};
					}

					column.getColumn().setText(metricType.getShortName());
					column.getColumn().setToolTipText(metricType.getToolTipText());
					column.getColumn().setWidth(100);
					column.setLabelProvider(new ForestTreeLabelProviders.MetricColumnLabelProvider(packageStyle == PackageStyle.FLAT, metricType));

					column.getColumn().addSelectionListener(selectionAdapter);
				}

				private void clear() {
					column.getColumn().setText("");
					column.setLabelProvider(new ForestTreeLabelProviders.EmptyColumnLabelProvider());
					column.getColumn().removeSelectionListener(selectionAdapter);
				}

			}
		}
