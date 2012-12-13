package exapus.model.view.evaluator;

import exapus.model.forest.FactForest;
import exapus.model.view.View;

public class ProjectCentricEvaluator extends Evaluator {

	protected ProjectCentricEvaluator(View v) {
		super(v);
	}

	@Override
	public FactForest getResult() {
		return getModelResult().getProjectCentricForest();
	}

	@Override
	public void evaluate() {
		// TODO Auto-generated method stub
		
	}

}
