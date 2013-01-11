package exapus.gui.editors.forest.tree;

import com.google.common.primitives.Ints;
import exapus.gui.editors.view.IViewEditorPage;
import exapus.gui.editors.view.ViewEditor;
import exapus.gui.views.forest.reference.ForestReferenceViewPart;
import exapus.model.forest.FactForest;
import exapus.model.forest.ForestElement;
import exapus.model.metrics.MetricType;
import exapus.model.store.Store;
import exapus.model.view.View;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.*;

import java.util.ArrayList;
import java.util.List;

public class ForestTreeEditor implements IEditorPart, IDoubleClickListener, IViewEditorPage {

    private IEditorSite editorSite;
    private IEditorInput editorInput;

    public static final String ID = "exapus.gui.views.forest.ForestTreeView";

    private List<MetricColumn> metricCols = new ArrayList<MetricColumn>();
    private MetricType chosen;
    private int idxFirstMetricCol;

    private TreeViewerColumn patternCol;
    private Combo comboGroupingPackages;

    private SortBy sorting = SortBy.NAME;
    private MetricType sortingMetric;

    private static enum SortBy {
        NAME, METRIC
    }

    private PackageStyle packageStyle = PackageStyle.NON_GROUPED;

    private static enum PackageStyle {
        NON_GROUPED(0, 3, "Non-grouped packages"), GROUPED(1, 1, "Grouped packages");

        private int index;
        private int expandedDepth;
        private String desc;

        private PackageStyle(int index, int expandedDepth, String desc) {
            this.index = index;
            this.expandedDepth = expandedDepth;
            this.desc = desc;
        }

        public static PackageStyle fromInt(int i) {
            if (NON_GROUPED.index == i) return NON_GROUPED;
            if (GROUPED.index == i) return GROUPED;
            throw new UnsupportedOperationException("No grouping for this index " + i);
        }
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
                switch (sorting) {
                    case NAME:
                        switch (packageStyle) {
                            case NON_GROUPED:
                                result = fe1.getName().toString().compareToIgnoreCase(fe2.getName().toString());
                                break;
                            case GROUPED:
                                result = fe1.getQName().toString().compareToIgnoreCase(fe2.getQName().toString());
                                break;
                        }
                        break;
                    case METRIC:
                        result = fe1.getMetric(sortingMetric).compareTo(fe2.getMetric(sortingMetric), packageStyle == PackageStyle.GROUPED);
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
        parent.setLayout(new GridLayout());

        // Toolbar

        ToolBarManager tbMgr = new ToolBarManager(SWT.NONE);
        tbMgr.createControl(parent);

        ToolItem sep = new ToolItem(tbMgr.getControl(), SWT.SEPARATOR);
        comboGroupingPackages = new Combo(tbMgr.getControl(), SWT.READ_ONLY);
        comboGroupingPackages.add(PackageStyle.NON_GROUPED.desc);
        comboGroupingPackages.add(PackageStyle.GROUPED.desc);
        comboGroupingPackages.select(PackageStyle.NON_GROUPED.index);

        comboGroupingPackages.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                changeGrouping(((Combo) selectionEvent.getSource()).getSelectionIndex());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {
                changeGrouping(((Combo) selectionEvent.getSource()).getSelectionIndex());
            }
        });

        comboGroupingPackages.pack();
        sep.setWidth(comboGroupingPackages.getSize().x);
        sep.setControl(comboGroupingPackages);

        tbMgr.getControl().pack();

        // Tree viewer

        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        viewer.getTree().setData(RWT.MARKUP_ENABLED, Boolean.FALSE); // do not enable html  markup ... otherwise names of parameterized types cannot be added

        viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        viewer.getTree().setHeaderVisible(true);
        viewer.getTree().setLinesVisible(true);
        viewer.setUseHashlookup(true);

        comparator = new MetricComparator();
        viewer.setComparator(comparator);

        viewer.setContentProvider(packageStyle == PackageStyle.GROUPED ? new ForestTreeGroupedPackagesContentProvider() : new ForestTreeContentProvider());

        patternCol = new TreeViewerColumn(viewer, SWT.NONE);
        patternCol.getColumn().setText("Pattern");
        patternCol.getColumn().setWidth(350);
        patternCol.setLabelProvider(new ForestTreeLabelProviders.PatternColumnLabelProvider(packageStyle == PackageStyle.GROUPED));

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


/*
        // We don't really need this anymore
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
*/

    }

    private void changeGrouping(int selectionIndex) {
        PackageStyle selected = PackageStyle.fromInt(selectionIndex);
        if (selected == packageStyle) return;

        packageStyle = selected;

        switch (selected) {
            case NON_GROUPED:
                viewer.setContentProvider(new ForestTreeContentProvider());
                break;
            case GROUPED:
                viewer.setContentProvider(new ForestTreeGroupedPackagesContentProvider());
                break;
        }

        patternCol.setLabelProvider(new ForestTreeLabelProviders.PatternColumnLabelProvider(packageStyle == PackageStyle.GROUPED));
        for (MetricColumn metricCol : metricCols) {
            metricCol.column.setLabelProvider(new ForestTreeLabelProviders.MetricColumnLabelProvider(packageStyle == PackageStyle.GROUPED, metricCol.metricType));
        }

        viewer.refresh();
        viewer.expandToLevel(packageStyle.expandedDepth);
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
        if (chosen != getView().getMetricType()) {
            chosen = getView().getMetricType();
            sorting = SortBy.NAME;
            sortingMetric = chosen;
            changeGrouping(PackageStyle.NON_GROUPED.index);

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

        String viewName = getEditorInput().getName();
        FactForest forest = Store.getCurrent().forestForRegisteredView(viewName);
        viewer.setInput(forest);
        viewer.expandToLevel(packageStyle.expandedDepth);
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

    private ImageDescriptor getImageDescriptor(String name) {
        return getWorkBench().getSharedImages().getImageDescriptor(name);
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

                        changeGrouping(PackageStyle.GROUPED.index);
                        if (comboGroupingPackages.getSelectionIndex() != PackageStyle.GROUPED.index) {
                            comboGroupingPackages.select(PackageStyle.GROUPED.index);
                        }

                        if (sorting == SortBy.METRIC) {
                            viewer.getTree().setSortDirection(comparator.change());
                        } else {
                            sorting = SortBy.METRIC;
                            comparator.setDirection(SWT.DOWN);
                        }
                        TreePath[] expanded = viewer.getExpandedTreePaths();
                        viewer.refresh();
                        viewer.setExpandedTreePaths(expanded);
                    }
                };
            }

            column.getColumn().setText(metricType.getShortName());
            column.getColumn().setWidth(100);
            column.setLabelProvider(new ForestTreeLabelProviders.MetricColumnLabelProvider(packageStyle == PackageStyle.GROUPED, metricType));

            column.getColumn().addSelectionListener(selectionAdapter);
        }

        private void clear() {
            column.getColumn().setText("");
            column.setLabelProvider(new ForestTreeLabelProviders.EmptyColumnLabelProvider());
            column.getColumn().removeSelectionListener(selectionAdapter);
        }

    }
}
