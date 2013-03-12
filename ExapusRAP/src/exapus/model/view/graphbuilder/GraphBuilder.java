package exapus.model.view.graphbuilder;

import exapus.model.forest.FactForest;
import exapus.model.view.View;
import exapus.model.visitors.IForestVisitor;

public abstract class GraphBuilder {
	
	private View view;
	
	protected View getView() {
		return view;
	}
	
	public GraphBuilder(View view) {
		this.view = view;
	}
	
	public static GraphBuilder forView(View v) {
        switch (v.getGraphDetails()) {
            case TOP_LEVEL_TAGS:
                return new TopLevelTagsGraphBuilder(v);
            case GROUPED_PACKAGES:
                return new GroupedPackagesGraphBuilder(v);
            case TOP_LEVEL_TYPES:
                return new TopLevelTypesGraphBuilder(v);
            default:
                return new TopLevelTypesGraphBuilder(v);
        }
	}
		
	protected ForestGraph graph;	

	public ForestGraph build(FactForest f) {
		graph = new ForestGraph(f);
		IForestVisitor v = newVisitor();
		if(view.getRenderable())
			f.acceptVisitor(v);	
		return graph;
	}
	
	protected abstract IForestVisitor newVisitor();
	
	
	
	
}
