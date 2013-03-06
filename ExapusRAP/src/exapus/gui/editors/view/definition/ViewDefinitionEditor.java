package exapus.gui.editors.view.definition;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.google.common.collect.Iterables;

import exapus.gui.editors.view.IViewEditorPage;
import exapus.gui.editors.view.ViewEditor;
import exapus.gui.util.Util;
import exapus.model.details.GraphDetails;
import exapus.model.metrics.MetricType;
import exapus.model.store.Store;
import exapus.model.view.Perspective;
import exapus.model.view.Selection;
import exapus.model.view.View;
import exapus.model.view.ViewFactory;

//todo: implement view change listener, also in other editor parts
public class ViewDefinitionEditor extends EditorPart implements IViewEditorPage{

	private ComboViewer comboVWPerspective;
	private Button checkRenderable;
	private TableViewer tableVWAPI;
	private TableViewer tableVWProjects;
	private ViewEditor viewEditor;
	private ComboViewer comboMetrics;
	private ComboViewer comboGraphDetails;
	private ComboViewer comboVWAPISource;
	private ToolBar toolbarAPI;
	private ToolBar toolbarProjects;
	private ComboViewer comboVWProjectSource;

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(3, false));

		//Perspective
		Label lblPerspective = new Label(parent, SWT.NONE);
		GridData gd_lblPerspective = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		lblPerspective.setLayoutData(gd_lblPerspective);
		lblPerspective.setText("Perspective:");

		comboVWPerspective = new ComboViewer(parent, SWT.READ_ONLY);
		comboVWPerspective.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		comboVWPerspective.setContentProvider(ArrayContentProvider.getInstance());
		comboVWPerspective.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object selected = selection.getFirstElement();
				if(selected instanceof Perspective) {
					getView().setPerspective((Perspective)selected);
					updateComboVWAPISource();
					updateComboVWProjectSource();
				}
			}
		});

		//API Source
		Label lblSource = new Label(parent, SWT.NONE);
		lblSource.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSource.setText("API Source:");

		comboVWAPISource = new ComboViewer(parent, SWT.READ_ONLY);
		comboVWAPISource.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		comboVWAPISource.setContentProvider(ArrayContentProvider.getInstance());

		comboVWAPISource.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object selected = selection.getFirstElement();
				if(selected instanceof String) {
					String selectedSourceName = (String) selected;
					if(selectedSourceName.equals(getCompleteAPIViewName()))
						getView().setAPISourceViewName(null);
					else
						getView().setAPISourceViewName(selectedSourceName);
				}
			}
		});





		//Packages
		Label lblAPILabel = new Label(parent, SWT.NONE);
		lblAPILabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblAPILabel.setText("APIs:");

		tableVWAPI = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL);
		toolbarAPI = new ToolBar(parent, SWT.VERTICAL);
		configureSelectionTableAndToolBar(tableVWAPI, toolbarAPI, Perspective.API_CENTRIC);


		//Project Source
		Label lblProjectSource = new Label(parent, SWT.NONE);
		lblProjectSource.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblProjectSource.setText("Project Source:");

		comboVWProjectSource = new ComboViewer(parent, SWT.READ_ONLY);
		comboVWProjectSource.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		comboVWProjectSource.setContentProvider(ArrayContentProvider.getInstance());
		comboVWProjectSource.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object selected = selection.getFirstElement();
				if(selected instanceof String) {
					String selectedSourceName = (String) selected;
					if(selectedSourceName.equals(getCompleteProjectViewName()))
						getView().setProjectSourceViewName(null);
					else
						getView().setProjectSourceViewName(selectedSourceName);
				}
			}
		});


		//Projects
		Label lblProjectsLabel = new Label(parent, SWT.NONE);
		lblProjectsLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblProjectsLabel.setText("Projects:");

		tableVWProjects = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL);
		toolbarProjects = new ToolBar(parent, SWT.VERTICAL);
		configureSelectionTableAndToolBar(tableVWProjects, toolbarProjects, Perspective.PROJECT_CENTRIC);

		// MetricType
		Label lblMetrics = new Label(parent, SWT.NONE);
		lblMetrics.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblMetrics.setText("Metrics:");

		comboMetrics = new ComboViewer(parent, SWT.READ_ONLY);
		comboMetrics.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		comboMetrics.setContentProvider(ArrayContentProvider.getInstance());
		comboMetrics.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object selected = selection.getFirstElement();
				if (selected instanceof MetricType) {
					getView().setMetricType((MetricType) selected);
				}
			}
		});

		//Graph details
		Label lblGraphDetails = new Label(parent, SWT.NONE);
		lblGraphDetails.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblGraphDetails.setText("Graph details:");

		comboGraphDetails = new ComboViewer(parent, SWT.READ_ONLY);
		comboGraphDetails.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		comboGraphDetails.setContentProvider(ArrayContentProvider.getInstance());
		comboGraphDetails.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object selected = selection.getFirstElement();
				if (selected instanceof GraphDetails) {
					getView().setGraphDetails((GraphDetails) selected);
				}
			}
		});

		//Renderable
		Label lblRenderable = new Label(parent, SWT.NONE);
		GridData gd_lblRenderable = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		lblRenderable.setLayoutData(gd_lblRenderable);

		checkRenderable = new Button(parent, SWT.CHECK);
		checkRenderable.setSelection(getView().getRenderable());

		GridData gd_checkRenderable = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		checkRenderable.setLayoutData(gd_checkRenderable);
		checkRenderable.setText("Render as graph.");

		checkRenderable.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getView().setRenderable(checkRenderable.getSelection());
				updateComboMetrics();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});


		comboVWPerspective.setInput(Perspective.supportedPerspectives());
		comboGraphDetails.setInput(GraphDetails.supportedDetails());

		updateComboMetrics();
	}

	private void configureSelectionTableAndToolBar(final TableViewer tableVW, ToolBar toolbar, final Perspective perspective) {
		Table tableAPI = tableVW.getTable();
		GridData gd_tableAPI = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_tableAPI.heightHint = tableAPI.getItemHeight() * 4;
		tableAPI.setLayoutData(gd_tableAPI);
		//tableAPI.setHeaderVisible(true);
		tableVW.setContentProvider(ArrayContentProvider.getInstance());

		TableViewerColumn APISelCol = new TableViewerColumn(tableVW, SWT.NONE);
		APISelCol.getColumn().setText("Name");
		APISelCol.getColumn().setWidth(250);

		APISelCol.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				Selection sel = (Selection) cell.getElement();
				cell.setText(sel.getNameString());
			}
		});
		TableViewerColumn APIScopeCol = new TableViewerColumn(tableVW, SWT.NONE);
		APIScopeCol.getColumn().setText("Scope");
		APIScopeCol.getColumn() .setWidth(150);
		APIScopeCol.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				Selection sel = (Selection) cell.getElement();
				cell.setText(sel.getScopeString());
			}
		});

		TableViewerColumn APITagCol = new TableViewerColumn(tableVW, SWT.NONE);
		APITagCol.getColumn().setText("Added Tag");
		APITagCol.getColumn() .setWidth(150);
		APITagCol.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				Selection sel = (Selection) cell.getElement();
				cell.setText(sel.getTagString());
			}
		});


		ToolItem toolItemAddAPI = new ToolItem(toolbar, SWT.PUSH);
		toolItemAddAPI.setToolTipText("Add");
		toolItemAddAPI.setImage(Util.getImageFromPlugin("add.gif"));
		toolItemAddAPI.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( final SelectionEvent event ) {
				showSelectionDialog(perspective);
			}
		});

		/*
	    final ToolItem toolItemEditAPI = new ToolItem(toolbar, SWT.PUSH);
	    toolItemEditAPI.setEnabled(false);
	    toolItemEditAPI.setText("Edit");
		 */

		final ToolItem toolItemDeleteAPI = new ToolItem(toolbar, SWT.PUSH);
		toolItemDeleteAPI.setEnabled(false);
		toolItemDeleteAPI.setToolTipText("Delete");
		toolItemDeleteAPI.setImage(Util.getImageFromPlugin("delete.gif"));

		toolItemDeleteAPI.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( final SelectionEvent event ) {
				IStructuredSelection sel = (IStructuredSelection) tableVW.getSelection();
				if(sel.isEmpty())
					return;
				Selection selectedSelection = (Selection) sel.getFirstElement();
				if(perspective.equals(Perspective.API_CENTRIC))
					getView().removeAPISelection(selectedSelection);
				if(perspective.equals(Perspective.PROJECT_CENTRIC))
					getView().removeProjectSelection(selectedSelection);
				updateControls();
			}
		});


		tableVW.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				boolean enabled = !event.getSelection().isEmpty();
				//toolItemEditAPI.setEnabled(enabled);
				toolItemDeleteAPI.setEnabled(enabled);
			}
		});
	}

	protected void showSelectionDialog(Perspective perspective) {
		SelectionDialog selectionDialog  = new SelectionDialog(getSite().getShell(), perspective, getView().getSourceViewName(perspective));
		int returnCode = selectionDialog.open();
		if(returnCode == IDialogConstants.OK_ID) {
			Selection newSelection = selectionDialog.getSelection();
			if(newSelection != null) {
				if(perspective.equals(Perspective.API_CENTRIC))
					getView().addAPISelection(newSelection);
				if(perspective.equals(Perspective.PROJECT_CENTRIC))
					getView().addProjectSelection(newSelection);
			}}
		updateControls();
	}

	private View getView() {
		return Store.getCurrent().getView(getEditorInput().getName());

	}

	@Override
	public void setFocus() {
		updateControls();
	}


	public void updateControls() {
		View view = getView();
		checkRenderable.setSelection(view.getRenderable());
		comboVWPerspective.setSelection(new StructuredSelection(view.getPerspective()));
		updateComboVWAPISource();
		updateComboVWProjectSource();
		tableVWAPI.setInput(Iterables.toArray(view.getAPISelections(),Object.class));
		tableVWProjects.setInput(Iterables.toArray(view.getProjectSelections(),Object.class));
		updateComboMetrics();
		comboGraphDetails.setSelection(new StructuredSelection(view.getGraphDetails()));
		enableControls(!getView().sealed());
	}

	private void enableControls(boolean enabled) {
		comboVWPerspective.getControl().setEnabled(enabled);
		comboVWAPISource.getControl().setEnabled(enabled);
		checkRenderable.setEnabled(enabled);
		tableVWAPI.getControl().setEnabled(enabled);
		tableVWProjects.getControl().setEnabled(enabled);
		comboMetrics.getControl().setEnabled(enabled);
		comboGraphDetails.getControl().setEnabled(enabled);
		toolbarProjects.setEnabled(enabled);
		toolbarAPI.setEnabled(enabled);
	}

	/*
	private String getCompleteViewName() {
		return getView().isAPICentric() ?
				ViewFactory.getCurrent().completePackageView().getName()
				: ViewFactory.getCurrent().completeProjectView().getName();
	}
	 */




	private String getCompleteAPIViewName() {
		return ViewFactory.getCurrent().completePackageView().getName();

	}

	private String getCompleteProjectViewName() {
		return ViewFactory.getCurrent().completeProjectView().getName();
	}


	private ArrayList<String> viewSourceNames(Perspective p) {
		ArrayList<String> elements = new ArrayList<String>();
		View thisView = getView();
		for(View v : Store.getCurrent().getRegisteredViews()) {
			if(v.getPerspective().equals(p)
					&& !v.equals(thisView)
					&& !thisView.hasTransitiveDependant(v))
				elements.add(v.getName());
		}
		return elements;
	}


	private void updateComboVWAPISource() {
		comboVWAPISource.setInput(viewSourceNames(Perspective.API_CENTRIC));
		String sourceViewName = getView().getAPISourceViewName();
		comboVWAPISource.setSelection(new StructuredSelection(sourceViewName == null ? getCompleteAPIViewName() : sourceViewName));
	}

	private void updateComboVWProjectSource() {
		comboVWProjectSource.setInput(viewSourceNames(Perspective.PROJECT_CENTRIC));
		String sourceViewName = getView().getProjectSourceViewName();
		comboVWProjectSource.setSelection(new StructuredSelection(sourceViewName == null ? getCompleteProjectViewName() : sourceViewName));
	}


	private void updateComboMetrics() {
		comboMetrics.setInput(MetricType.supportedMetrics(getView().getRenderable()));
		comboMetrics.setSelection(new StructuredSelection(getView().getMetricType()));
	}

	public void setViewEditor(ViewEditor viewEditor) {
		this.viewEditor = viewEditor;
	}

}
