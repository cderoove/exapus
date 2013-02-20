package exapus.model.view.graphdrawer;

import exapus.gui.editors.forest.graph.IEdgeFormatter;
import exapus.gui.editors.forest.graph.IGraphFormatter;
import exapus.gui.editors.forest.graph.INodeFormatter;
import exapus.model.view.View;

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
