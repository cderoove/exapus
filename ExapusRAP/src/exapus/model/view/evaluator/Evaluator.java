package exapus.model.view.evaluator;

import exapus.model.forest.ExapusModel;
import exapus.model.forest.FactForest;
import exapus.model.forest.InboundFactForest;
import exapus.model.forest.OutboundFactForest;
import exapus.model.view.Perspective;
import exapus.model.view.View;

public abstract class Evaluator {

	private View view;
	protected ExapusModel modelResult;

	protected ExapusModel getModelResult() {
		return modelResult;
	}
		
	public static Evaluator forView(View v) {
		if(v.isAPICentric())
			return new APICentricEvaluator(v);
		if(v.isProjectCentric())
			return new ProjectCentricEvaluator(v);
		return null;
	}
	
	public static FactForest evaluate(View v) {
		Evaluator e = forView(v);
		e.evaluate();
		return e.getResult();
	}
	
	protected Evaluator(View v) {
		view = v;
		modelResult = new ExapusModel();
	}

	public View getView() {
		return view;
	}
	
	public abstract FactForest getResult();
	
	public abstract void evaluate();
	



}
