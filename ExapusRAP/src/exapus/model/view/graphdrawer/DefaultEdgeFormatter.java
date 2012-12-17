package exapus.model.view.graphdrawer;

import java.util.Collections;

import exapus.gui.editors.forest.graph.Edge;
import exapus.gui.editors.forest.graph.IEdgeFormatter;

public class DefaultEdgeFormatter implements IEdgeFormatter {

	@Override
	public Iterable<String> decorations(Edge e) {
		return Collections.emptyList();
	}

}
