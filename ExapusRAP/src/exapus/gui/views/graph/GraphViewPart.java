package exapus.gui.views.graph;

import java.io.IOException;
import java.util.Collections;

import exapus.gui.views.SelectedForestElementBrowserViewPart;
import exapus.model.forest.ForestElement;

public class GraphViewPart extends SelectedForestElementBrowserViewPart {

	public static final String ID = "exapus.gui.views.GraphView";

	private Graph graph;

	public void setGraph(Graph g ) {
		graph = g;
	}

	private void ensureGraph() {
		if (graph==null) 
			graph = dummyGraph();
	}

	public static Graph dummyGraph() {
		Graph g = new Graph();
		Node n1 = new Node();
		Node n2 = new Node();
		Edge e = new Edge(n1,n2);
		g.add(e);
		return g;
	}


	public void generateSVG() {		
		GraphViz dot = new GraphViz(graph);
		try {
			dot.toSVGFile(new IGraphFormatter() {
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

	}
	@Override
	protected String textToRender(ForestElement fe) {
		ensureGraph();
		generateSVG();
		return "<html><body><p>foooooooooooo</p></body></html>";
	}


}
