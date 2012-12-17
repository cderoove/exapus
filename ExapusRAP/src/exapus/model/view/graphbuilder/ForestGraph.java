package exapus.model.view.graphbuilder;

import exapus.gui.editors.forest.graph.Graph;
import exapus.model.forest.FactForest;

public class ForestGraph extends Graph {
	
	private FactForest forest;
	
	public ForestGraph(FactForest f) {
		super();
		forest = f;
	}
	
}
