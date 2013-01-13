package exapus.gui.editors.forest.graph;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import exapus.gui.editors.SelectedForestElementImageBrowserViewPart;
import exapus.gui.editors.view.IViewEditorPage;
import exapus.gui.editors.view.ViewEditor;
import exapus.model.forest.FactForest;
import exapus.model.forest.ForestElement;
import exapus.model.store.Store;

public class ForestGraphEditor extends SelectedForestElementImageBrowserViewPart implements IEditorPart, IViewEditorPage {
	public ForestGraphEditor() {
	}
	
	private IEditorSite editorSite;
	private IEditorInput editorInput;
	private ViewEditor viewEditor;
		
	private int zoom = 100;
	
	public static final String ID = "exapus.gui.views.forest.ForestGraphView";
	private final static String GRAPH_KEY = "graphviz";

	@Override
	protected String textToRender(ForestElement fe) {
		return textToRender();
	}
	
	//Larger seems to crash most recent Safari
	private static long FILE_SIZE_THRESHOLD = 3 * 1024 * 1024;
	private static boolean fileHasSafeSize(File f) {
		return f.length() <= FILE_SIZE_THRESHOLD;
	}
	
	
	/*
	private boolean askRenderConfirmation() {
		    String title = "Confirm graph rendering.";
		    String msg = "Graph might be too large to render. Continue rendering anyway?";
		    return MessageDialog.openConfirm(getSite().getShell(), title, msg);
	}
	*/
	
	

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		/*
		 * TODO: Cannot get the layout of the spinner right. Without one for now ...
		 * 
		parent.setLayout(new GridLayout(2, false));
	    Label label = new Label(parent, SWT.NONE);
	    label.setText("Zoom:");
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	    
		final Spinner spinner = new Spinner(parent, SWT.NONE);
		spinner.setMinimum(1);
		spinner.setMaximum(100);
		spinner.setSelection(100);
		spinner.setPageIncrement(20);
		spinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				zoom = spinner.getSelection();
				updateBrowser();
			}
		});
		spinner.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		super.createPartControl(parent);
		browser.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true, 2, 1));
		*/
	}

	

	private String textForGraph() {
		StringBuffer html = new StringBuffer();
		html.append("<html><body><p>");
		html.append("<img src=\"");
		html.append(createImageUrl(GRAPH_KEY));
		//html.append("\" width=" + zoom + "% />");
		html.append("\" />");
		html.append("</p></body></html>");
		return html.toString();
	}

	private String textForConfirm() {
		StringBuffer html = new StringBuffer();
		html.append("<html><body><p>Graph might be too large to render. ");
		html.append("Render large <a href=\"");
		html.append(createImageUrl(GRAPH_KEY));
		html.append("\"/>");
		html.append("graph");
		html.append("</a> anyway.");
		html.append("</p></body></html>");
		return html.toString();
	}

	
	@Override
	protected String textToRender() {
		File imageFile = Store.getCurrent().graphForRegisteredView(editorInput.getName());
		if(imageFile != null) {
			registerImage(GRAPH_KEY, imageFile);
			if(fileHasSafeSize(imageFile)) 
				return textForGraph();
			else
				return textForConfirm();
		}
		return "";
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
		return "Graph view on: " + editorInput.getName();
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




}
