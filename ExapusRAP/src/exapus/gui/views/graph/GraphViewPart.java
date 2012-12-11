package exapus.gui.views.graph;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;

import org.eclipse.rwt.RWT;

import exapus.gui.views.SelectedForestElementImageBrowserViewPart;
import exapus.model.forest.ForestElement;

public class GraphViewPart extends SelectedForestElementImageBrowserViewPart {

	public static final String ID = "exapus.gui.views.GraphView";

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


}
