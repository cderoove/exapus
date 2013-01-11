package exapus.model.view.graphdrawer;

import java.awt.image.BufferedImage;
import java.io.IOException;

import exapus.gui.editors.forest.graph.Graph;
import exapus.gui.editors.forest.graph.GraphViz;
import exapus.gui.editors.forest.graph.IEdgeFormatter;
import exapus.gui.editors.forest.graph.IGraphFormatter;
import exapus.gui.editors.forest.graph.INodeFormatter;
import exapus.model.view.View;
import exapus.model.view.graphbuilder.ForestGraph;

public class ContainmentGraphDrawer extends GraphDrawer {
	
	public ContainmentGraphDrawer(View v) {
        super(v);
	}

	@Override
	protected IGraphFormatter getGraphFormatter() {
		return new DefaultGraphFormatter();
	}

	@Override
	protected INodeFormatter getNodeFormatter() {
		//return new DefaultNodeFormatter();
		return new QuartileBasedNodeFormatter(view.getMetricType());
	}

	@Override
	protected IEdgeFormatter getEdgeFormatter() {
		return new DefaultEdgeFormatter();
	}
	

}
