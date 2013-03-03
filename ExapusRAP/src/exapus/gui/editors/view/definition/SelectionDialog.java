package exapus.gui.editors.view.definition;

import java.util.Collections;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import org.eclipse.core.databinding.validation.ValidationStatus;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import exapus.gui.editors.forest.tree.ForestTreeLabelProviders;
import exapus.model.forest.ExapusModel;
import exapus.model.forest.FactForest;
import exapus.model.forest.ForestElement;
import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.QName;
import exapus.model.store.Store;
import exapus.model.view.Perspective;
import exapus.model.view.Scope;
import exapus.model.view.ScopedSelection;
import exapus.model.view.Selection;
import exapus.model.view.UniversalSelection;
import exapus.model.view.View;

public class SelectionDialog extends Dialog {

	private Perspective perspective;
	private ComboViewer selectionTypeComboVW;
	private Composite scopedSelectionComposite;
	private ComboViewer scopedSelectionScopeComboVW;

	//private Text scopedSelectionNameText;
	private ComboViewer scopedSelectionNameComboVW;

	private Selection selection;
	private Text scopedSelectionTagText;

	private String sourceViewName;
	private Label scopeDescriptionLabel;

	public Selection getSelection() {
		return selection;
	}

	public SelectionDialog(Shell parentShell, Perspective perspective, String sourceViewName) {
		super(parentShell);
		this.perspective = perspective;
		this.sourceViewName = sourceViewName;

	}

	//adapted from RAP controls demo
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		if(perspective.equals(Perspective.API_CENTRIC))
			shell.setText("Select Referenced API Elements");
		if(perspective.equals(Perspective.PROJECT_CENTRIC))
			shell.setText("Select Referencing Project Elements");
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
		lblType.setText("Kind:");

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
		scopedSelectionComposite.setLayout(new GridLayout(3, false));
		scopedSelectionComposite.setVisible(false);

		Label scopeTypeLabel = new Label(scopedSelectionComposite, SWT.NONE);
		scopeTypeLabel.setText("Scope:");
		scopeTypeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));	

		scopedSelectionScopeComboVW = new ComboViewer(scopedSelectionComposite, SWT.READ_ONLY);
		GridData gd_scopedSelectionScopeComboVW = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		scopedSelectionScopeComboVW.getControl().setLayoutData(gd_scopedSelectionScopeComboVW);
		scopedSelectionScopeComboVW.setContentProvider(ArrayContentProvider.getInstance());
		scopedSelectionScopeComboVW.setInput(Scope.supportedSelectionScopes());

		Label spacer = new Label(scopedSelectionComposite, SWT.NONE);
		spacer.setText("");
		spacer.setEnabled(false);
		spacer.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		scopeDescriptionLabel = new Label(scopedSelectionComposite, SWT.NONE | SWT.WRAP);
		GridData d = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		d.widthHint = 500;
		scopeDescriptionLabel.setLayoutData(d);
		
		
		Label nameLabel = new Label(scopedSelectionComposite, SWT.NONE);
		nameLabel.setText("Name:");
		nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

		/*
		//Text field with auto-completion
		scopedSelectionNameText = new Text(scopedSelectionComposite, SWT.BORDER);
		scopedSelectionNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,1,1));

		final SimpleContentProposalProvider proposalProvider = new SimpleContentProposalProvider(getProposalStrings());
		proposalProvider.setFiltering(true);
		ContentProposalAdapter adapter = new ContentProposalAdapter(scopedSelectionNameText, new TextContentAdapter(), proposalProvider, null, null);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

		scopedSelectionScopeComboVW.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				proposalProvider.setProposals(getProposalStrings());

			}
		});
		 */


		//Combo with free-form text entry (necessary for prefix)
		scopedSelectionNameComboVW = new ComboViewer(scopedSelectionComposite, SWT.NONE);
		scopedSelectionNameComboVW.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		scopedSelectionNameComboVW.setContentProvider(ArrayContentProvider.getInstance());
		scopedSelectionNameComboVW.setSorter(new ViewerSorter());
		scopedSelectionNameComboVW.getCombo().setEnabled(false);


		final Button button = new Button(scopedSelectionComposite, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));	
		button.setText("...");
		button.setEnabled(false);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new ForestTreeLabelProviders.PatternColumnLabelProvider(false), new ScopeSelectionForestTreeContentProvider());

				dialog.setInput(getProposalFactForest());
				dialog.setTitle("Select an existing forest element.");
				dialog.setMessage("The qualified name of the selected element will determine the " + getSelectedScope() +".");
				dialog.setComparator(new ViewerSorter());
				dialog.setAllowMultiple(false);

				dialog.setValidator(new ISelectionStatusValidator() {
					@Override
					public IStatus validate(Object[] selection) {
						if(selection.length != 1)
							return ValidationStatus.error("Exactly one forest element should be selected.");
						ForestElement element = (ForestElement) selection[0];
						if(element == null)
							return ValidationStatus.error("The selected object is not a forest element.");
						if(!isElementCompatibleWithSelectedScope(element))
							return ValidationStatus.error("Element " + element.getQName().toString() + " is not compatible with " + getSelectedScope() + ".");						
						return ValidationStatus.info("Name of selected element: " + element.getQName().toString());
					}
				});
				if(dialog.open() == Window.OK) {
					Object[] selection = dialog.getResult(); 
					ForestElement element = (ForestElement) selection[0];
					scopedSelectionNameComboVW.getCombo().setText(element.getQName().toString());
				}

			}
		});

		Label tagLabel = new Label(scopedSelectionComposite, SWT.NONE);
		tagLabel.setText("Add Tag:");
		tagLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		scopedSelectionTagText = new Text(scopedSelectionComposite, SWT.BORDER);
		scopedSelectionTagText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,2,1));
		scopedSelectionTagText.setEnabled(false);



		scopedSelectionScopeComboVW.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				scopedSelectionNameComboVW.getControl().setEnabled(true);
				scopedSelectionTagText.setEnabled(true);
				button.setEnabled(true);
				scopedSelectionNameComboVW.setInput(getProposalStrings());
				scopeDescriptionLabel.setText(getSelectedScope().getDescription());
			}
		});



		return composite;
	}


	private FactForest getProposalFactForest() {
		if(sourceViewName != null && Store.getCurrent().hasRegisteredView(sourceViewName)) {
			View sourceView = Store.getCurrent().getView(sourceViewName);
			if(sourceView.getPerspective().equals(perspective))
				return sourceView.evaluate();
		}
		ExapusModel workspaceModel = Store.getCurrent().getWorkspaceModel();
		return perspective.equals(Perspective.API_CENTRIC) ? workspaceModel.getAPICentricForest() : workspaceModel.getProjectCentricForest();
	}


	private boolean isElementCompatibleWithSelectedScope(ForestElement element) {
		Scope selectedScope = getSelectedScope();
		if(selectedScope == null)
			return false;

		if(selectedScope.equals(Scope.ROOT_SCOPE)) 
			return element instanceof PackageTree;

		if(selectedScope.equals(Scope.PACKAGE_SCOPE))
			return element instanceof PackageLayer;

		if(selectedScope.equals(Scope.TYPE_SCOPE)) 
			return (element instanceof Member) && ((Member) element).getElement().declaresType();

		if(selectedScope.equals(Scope.METHOD_SCOPE)) 
			return (element instanceof Member) && ((Member) element).getElement().isMethod();

		if(selectedScope.equals(Scope.PREFIX_SCOPE))
			return element instanceof PackageLayer;

		return false;

	}

	private Iterable<? extends ForestElement> getProposalForestElements() {
		Scope selectedScope = getSelectedScope();
		if(selectedScope == null)
			return Collections.emptyList();
		FactForest forest = getProposalFactForest();
		if(selectedScope.equals(Scope.ROOT_SCOPE)) 
			return forest.getPackageTrees();
		if(selectedScope.equals(Scope.PACKAGE_SCOPE))
			return forest.getAllPackageLayers();
		if(selectedScope.equals(Scope.TYPE_SCOPE))
			return Iterables.filter(forest.getAllMembers(),new Predicate<Member>() {
				@Override
				public boolean apply(Member m) {
					return m.getElement().declaresType();
				}
			});
		if(selectedScope.equals(Scope.METHOD_SCOPE))
			return Iterables.filter(forest.getAllMembers(),new Predicate<Member>() {
				@Override
				public boolean apply(Member m) {
					return m.getElement().isMethod();
				}
			});
		if(selectedScope.equals(Scope.PREFIX_SCOPE))
			return forest.getAllPackageLayers();
		return Collections.emptyList();
	}

	private String[] getProposalStrings() {
		return Iterables.toArray(Iterables.transform(getProposalForestElements(), new Function<ForestElement, String>() {
			public String apply(ForestElement e) {
				return e.getQName().toString();
			}
		}),String.class);
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

	@SuppressWarnings("unchecked")
	private Class<Selection> getSelectedType() {
		IStructuredSelection selType = (IStructuredSelection) selectionTypeComboVW.getSelection();
		if(selType.isEmpty())
			return null;
		return (Class<Selection>) selType.getFirstElement();
	}


	@SuppressWarnings("unused")
	private Scope getSelectedScope() {
		Class<Selection> selectedType = getSelectedType();
		if(selectedType == null || UniversalSelection.class.equals(selectedType)) 
			return null;		
		IStructuredSelection selScope = (IStructuredSelection) scopedSelectionScopeComboVW.getSelection();
		if(selScope.isEmpty())
			return null;
		return (Scope) selScope.getFirstElement();
	}


	private void updateSelection() {
		selection = null;
		Class<Selection> selectedType = getSelectedType();
		if(selectedType == null)
			return;
		if(selectedType.equals(UniversalSelection.class)) {
			selection = UniversalSelection.getCurrent();
			return;
		}
		if(selectedType.equals(ScopedSelection.class)) {
			Scope selectedScope = getSelectedScope();
			if(selectedScope == null)
				return;
			//QName name = new QName(scopedSelectionNameText.getText());
			QName name = new QName(scopedSelectionNameComboVW.getCombo().getText());

			selection = new ScopedSelection(name, selectedScope);

			String scopedSelectionTag = scopedSelectionTagText.getText().trim();
			if(!scopedSelectionTag.isEmpty())
				((ScopedSelection) selection).setTag(scopedSelectionTag);
			return;
		}
	}

}




