package exapus.gui.editors.forest.graph;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.rwt.RWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

import exapus.gui.editors.SelectedForestElementImageBrowserViewPart;
import exapus.model.forest.FactForest;
import exapus.model.forest.ForestElement;
import exapus.model.store.Store;

public class ForestGraphEditor extends SelectedForestElementImageBrowserViewPart implements IEditorPart {
	
	private IEditorSite editorSite;
	private IEditorInput editorInput;
	
	public static final String ID = "exapus.gui.views.forest.ForestGraphView";

	private final static String GRAPH_KEY = "graphviz";

	private Graph graph;

	public void setGraph(Graph g ) {
		graph = g;
	}

	public static Graph dummyGraph() {
		Graph g = new Graph();
		Node n1 = new Node();
		Node n2 = new Node();
		Edge e = new Edge(n1,n2);
		g.add(e);
		return g;
	}


	protected BufferedImage getGraphImage() {
		GraphViz dot = new GraphViz(dummyGraph());
		BufferedImage image = null;
		try {
			image = dot.toImage(new IGraphFormatter() {
				@Override
				public Iterable<String> decorations(Graph g) {
					return Collections.emptyList();
				}
			},
			new INodeFormatter() {
				@Override
				public String label(Node n) {
					return n.getIdentifier();
				}

				@Override
				public Iterable<String> decorations(Node n) {
					return Collections.emptyList();
				}
			},
			new IEdgeFormatter() {
				@Override
				public Iterable<String> decorations(Edge e) {
					return Collections.emptyList();
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return image;
	}

	@Override
	protected String textToRender(ForestElement fe) {
		registerImage(GRAPH_KEY, getGraphImage());
		StringBuffer html = new StringBuffer();
		html.append("<img src=\"");
		html.append(createImageUrl(GRAPH_KEY));
		html.append("\"/>");
		return html.toString();
	}

    protected void setInput(IEditorInput input) {
    	editorInput = input;
		String registeredName = input.getName();
		//TODO: compute forest
		//FactForest forest = Store.getCurrent().getForest(registeredName);
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



}
