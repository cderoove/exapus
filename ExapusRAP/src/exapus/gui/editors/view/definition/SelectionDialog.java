package exapus.gui.editors.view.definition;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import exapus.model.forest.QName;
import exapus.model.view.Perspective;
import exapus.model.view.Scope;
import exapus.model.view.ScopedSelection;
import exapus.model.view.Selection;
import exapus.model.view.UniversalSelection;

public class SelectionDialog extends Dialog {

	private Perspective perspective;
	private ComboViewer selectionTypeComboVW;
	private Composite scopedSelectionComposite;
	private ComboViewer scopedSelectionScopeComboVW;
	private Text scopedSelectionNameText;

	private Selection selection;
	private Text scopedSelectionTagText;

	public Selection getSelection() {
		return selection;
	}

	public SelectionDialog(Shell parentShell, Perspective perspective) {
		super(parentShell);
		this.perspective = perspective;
	}

	//adapted from RAP controls demo
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		if(perspective.equals(Perspective.API_CENTRIC))
			shell.setText("New API Selection");
		if(perspective.equals(Perspective.PROJECT_CENTRIC))
			shell.setText("New Project Selection");
		shell.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				initializeBounds();
			}
		});
	}

	protected Control createDialogArea(final Composite p) {
		Composite composite = (Composite) super.createDialogArea(p);
		composite.setLayout(new GridLayout(2, false));

		Label lblType = new Label(composite, SWT.NONE);
		GridData gd_lblType = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		lblType.setLayoutData(gd_lblType);
		lblType.setText("Type:");

		selectionTypeComboVW = new ComboViewer(composite, SWT.READ_ONLY);
		GridData gd_comboVWType = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		Combo combo = selectionTypeComboVW.getCombo();
		combo.setLayoutData(gd_comboVWType);
		selectionTypeComboVW.setContentProvider(ArrayContentProvider.getInstance());
		selectionTypeComboVW.setInput(Selection.supportedSelections());
		selectionTypeComboVW.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				Class<Selection> type = (Class<Selection>) element;
				return type.getSimpleName();
			}
		});



		selectionTypeComboVW.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object selected = selection.getFirstElement();
				if(selected.equals(UniversalSelection.class))
					scopedSelectionComposite.setVisible(false);
				if(selected.equals(ScopedSelection.class))
					scopedSelectionComposite.setVisible(true);
			}
		});


		//TODO: figure out how to make this composite align with the grid cell in which it is placed
		scopedSelectionComposite = new Composite(composite, SWT.NONE);
		scopedSelectionComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		scopedSelectionComposite.setLayout(new GridLayout(2, false));
		scopedSelectionComposite.setVisible(false);

		Label scopeTypeLabel = new Label(scopedSelectionComposite, SWT.NONE);
		scopeTypeLabel.setText("Scope:");
		scopeTypeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));	
		scopedSelectionScopeComboVW = new ComboViewer(scopedSelectionComposite, SWT.READ_ONLY);
		GridData gd_scopedSelectionScopeComboVW = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		scopedSelectionScopeComboVW.getControl().setLayoutData(gd_scopedSelectionScopeComboVW);
		scopedSelectionScopeComboVW.setContentProvider(ArrayContentProvider.getInstance());
		scopedSelectionScopeComboVW.setInput(Scope.supportedSelectionScopes());
		

		Label nameLabel = new Label(scopedSelectionComposite, SWT.NONE);
		nameLabel.setText("Name:");
		nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		scopedSelectionNameText = new Text(scopedSelectionComposite, SWT.BORDER);
		scopedSelectionNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,1,1));
		
		Label tagLabel = new Label(scopedSelectionComposite, SWT.NONE);
		tagLabel.setText("Tag:");
		tagLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		scopedSelectionTagText = new Text(scopedSelectionComposite, SWT.BORDER);
		scopedSelectionTagText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,1,1));
		return composite;
	}


	protected void createButtonsForButtonBar(final Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false);
		createButton(parent, IDialogConstants.OK_ID, "OK", true);
	}

	protected void buttonPressed(final int buttonId) {
		if(buttonId == IDialogConstants.OK_ID) {
			updateSelection();
			setReturnCode(IDialogConstants.OK_ID);
			close();
		} else {
			selection = null;
		}
		super.buttonPressed(buttonId);
	}

	private void updateSelection() {
		selection = null;
		IStructuredSelection selType = (IStructuredSelection) selectionTypeComboVW.getSelection();
		if(selType.isEmpty())
			return;
		Object selectedType = selType.getFirstElement();
		if(selectedType.equals(UniversalSelection.class)) {
			selection = UniversalSelection.getCurrent();
			return;
		}
		
		if(selectedType.equals(ScopedSelection.class)) {
			QName name = new QName(scopedSelectionNameText.getText());
			IStructuredSelection selScope = (IStructuredSelection) scopedSelectionScopeComboVW.getSelection();
			if(selScope.isEmpty())
				return;
			selection = new ScopedSelection(name, (Scope) selScope.getFirstElement());
			String scopedSelectionTag = scopedSelectionTagText.getText().trim();
			if(!scopedSelectionTag.isEmpty())
				((ScopedSelection) selection).setTag(scopedSelectionTag);
			return;
		}


	}
}




