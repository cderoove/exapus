package exapus.model.view.graphdrawer;

import java.util.Collections;

import exapus.gui.editors.forest.graph.Graph;
import exapus.gui.editors.forest.graph.IGraphFormatter;

public class DefaultGraphFormatter implements IGraphFormatter {

	@Override
	public Iterable<String> decorations(Graph g) {
		return Collections.emptyList();
	}

}
