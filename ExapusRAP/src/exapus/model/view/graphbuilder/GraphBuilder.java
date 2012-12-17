package exapus.model.view.graphbuilder;

import exapus.gui.editors.forest.graph.Graph;
import exapus.model.forest.FactForest;
import exapus.model.view.View;
import exapus.model.visitors.IForestVisitor;

public abstract class GraphBuilder {
	
	public static GraphBuilder forView(View v) {
		return new ContainmentGraphBuilder(v);
	}
		
	protected ForestGraph graph;	

	public ForestGraph build(FactForest f) {
		graph = new ForestGraph(f);
		IForestVisitor v = newVisitor();
		f.acceptVisitor(v);
		return graph;
	}
	
	protected abstract IForestVisitor newVisitor();
	
	
	
	
}
