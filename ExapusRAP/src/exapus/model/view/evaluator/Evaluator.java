package exapus.model.view.evaluator;

import exapus.model.forest.ExapusModel;
import exapus.model.forest.FactForest;
import exapus.model.forest.InboundFactForest;
import exapus.model.forest.OutboundFactForest;
import exapus.model.view.Perspective;
import exapus.model.view.View;

public abstract class Evaluator {

	private View view;
	private ExapusModel modelResult;

	protected ExapusModel getModelResult() {
		return modelResult;
	}
	
	public static Evaluator forView(View v) {
		if(v.isAPICentric())
			new APICentricEvaluator(v);
		if(v.isProjectCentric())
			new ProjectCentricEvaluator(v);
		return null;
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
