package exapus.gui.views.graph;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.IServiceHandler;

import exapus.gui.views.SelectedForestElementBrowserViewPart;
import exapus.model.forest.ForestElement;

public class GraphViewPart extends SelectedForestElementBrowserViewPart {

	private class GraphServiceHandler implements IServiceHandler {
		public void service() throws IOException, ServletException {
			String id = RWT.getRequest().getParameter("imageId");
			BufferedImage image = (BufferedImage)RWT.getSessionStore().getAttribute(id);
			HttpServletResponse response = RWT.getResponse();
			response.setContentType("image/png");
			ServletOutputStream out = response.getOutputStream();
			ImageIO.write(image, "png", out );
		}
	}


	public GraphViewPart() {
		super();
		registerGraphServiceHandler();
	}

	private void registerGraphServiceHandler() {
		RWT.getServiceManager().registerServiceHandler(SERVICE_HANDLER, new GraphServiceHandler());
	}

	public static final String ID = "exapus.gui.views.GraphView";

	private final static String SERVICE_HANDLER = "graphServiceHandler";
	private final static String GRAPH_KEY = "graphKey";

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


	protected BufferedImage getGraphImage() {
		GraphViz dot = new GraphViz(graph);
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
		ensureGraph();
		storeGraphImage();
		StringBuffer html = new StringBuffer();
		html.append("<img src=\"");
		html.append(createImageUrl(GRAPH_KEY));
		html.append("\"/>");
		return html.toString();
	}

	
	private void storeGraphImage() {
		RWT.getSessionStore().setAttribute(GRAPH_KEY, getGraphImage());
	}


	private Object createImageUrl(String imageKey) {
		StringBuffer url = new StringBuffer();
		url.append(RWT.getRequest().getContextPath());
		url.append(RWT.getRequest().getServletPath());
		url.append("?");
		url.append(IServiceHandler.REQUEST_PARAM);
		url.append("=");
		url.append(SERVICE_HANDLER);
		url.append("&imageId=");
		url.append(imageKey);
		url.append("&nocache=");
		url.append(System.currentTimeMillis());
		String encodedURL = RWT.getResponse().encodeURL(url.toString());
		return encodedURL;
	}


}
