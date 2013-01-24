package exapus.gui.editors.view.definition;

import java.util.Collections;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import exapus.model.forest.ExapusModel;
import exapus.model.forest.FactForest;
import exapus.model.forest.ForestElement;
import exapus.model.forest.Member;
import exapus.model.forest.QName;
import exapus.model.store.Store;
import exapus.model.view.View;
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
	
	private String sourceViewName;

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
		
		final SimpleContentProposalProvider proposalProvider = new SimpleContentProposalProvider(getProposalStrings());
		ContentProposalAdapter adapter = new ContentProposalAdapter(scopedSelectionNameText, new TextContentAdapter(), proposalProvider, null, null);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		
		scopedSelectionScopeComboVW.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				proposalProvider.setProposals(getProposalStrings());
				
			}
		});
		
		Label tagLabel = new Label(scopedSelectionComposite, SWT.NONE);
		tagLabel.setText("Tag:");
		tagLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		scopedSelectionTagText = new Text(scopedSelectionComposite, SWT.BORDER);
		scopedSelectionTagText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,1,1));
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
			QName name = new QName(scopedSelectionNameText.getText());
			selection = new ScopedSelection(name, selectedScope);
			
			String scopedSelectionTag = scopedSelectionTagText.getText().trim();
			if(!scopedSelectionTag.isEmpty())
				((ScopedSelection) selection).setTag(scopedSelectionTag);
			return;
		}
	}
	
}




