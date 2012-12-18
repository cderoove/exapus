package exapus.gui.editors.view.definition;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Button;

import exapus.model.store.Store;
import exapus.model.view.Perspective;
import exapus.model.view.View;

//todo: implement view change listener, also in other editor parts
public class ViewDefinitionEditor extends EditorPart {

	private ComboViewer comboVWPerspective;
	private Button checkRenderable;

	public ViewDefinitionEditor() {
	}

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

		comboVWPerspective = new ComboViewer(parent, SWT.NONE);
		GridData gd_ComboPerspective = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		Combo combo = comboVWPerspective.getCombo();
		combo.setLayoutData(gd_ComboPerspective);
		comboVWPerspective.setContentProvider(ArrayContentProvider.getInstance());
		comboVWPerspective.setInput(View.supportedPerspectives());
		comboVWPerspective.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object selected = selection.getFirstElement();
				if(selected instanceof Perspective) {
					getView().setPerspective((Perspective)selected);
					hasUpdatedView();
				}
			}
		});
		
		
		//Renderable
		Label lblRenderable = new Label(parent, SWT.NONE);
		GridData gd_lblRenderable = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		lblRenderable.setLayoutData(gd_lblRenderable);
		
		checkRenderable = new Button(parent, SWT.CHECK);
		GridData gd_checkRenderable = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		checkRenderable.setLayoutData(gd_checkRenderable);
		checkRenderable.setText("Render as graph.");

		checkRenderable.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getView().setRenderable(checkRenderable.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		//APIs	
		Label lblAPILabel = new Label(parent, SWT.NONE);
		lblAPILabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblAPILabel.setText("APIs:");

		TableViewer tableVWAPI = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL);
		GridData gd_tableAPI = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		tableVWAPI.getControl().setLayoutData(gd_tableAPI);

		Button btnAPIAdd = new Button(parent, SWT.NONE);
		btnAPIAdd.setText("Add...");
		GridData gd_btnAPIAdd = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
		btnAPIAdd.setLayoutData(gd_btnAPIAdd);



		//Projects
		Label lblProjectsLabel = new Label(parent, SWT.NONE);
		lblProjectsLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblProjectsLabel.setText("Projects:");

		TableViewer tableVWProjects = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL);
		GridData gd_tableProjects = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		tableVWProjects.getTable().setLayoutData(gd_tableProjects);


		Button btnProjectsAdd = new Button(parent, SWT.NONE);
		btnProjectsAdd.setText("Add...");
		GridData gd_btnProjectsAdd = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
		btnProjectsAdd.setLayoutData(gd_btnProjectsAdd);


	}

	private View getView() {
		return Store.getCurrent().getView(getEditorInput().getName());

	}

	@Override
	public void setFocus() {
		updateControls();
	}

	private void hasUpdatedView() {
		
	}

	private void updateControls() {
		View view = getView();
		comboVWPerspective.setSelection(new StructuredSelection(view.getPerspective()));
		checkRenderable.setSelection(getView().getRenderable());

	}

}
