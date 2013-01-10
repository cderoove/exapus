package exapus.model.view.evaluator;

import exapus.model.forest.ExapusModel;
import exapus.model.forest.FactForest;
import exapus.model.forest.InboundFactForest;
import exapus.model.forest.OutboundFactForest;
import exapus.model.metrics.Metrics;
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

    protected void calculateMetrics(FactForest forest) {
        if (getView().getMetrics() != null) {
            long startTime = System.currentTimeMillis();

            if (getView().getMetrics() == Metrics.ALL) {
                for (Metrics metric : Metrics.supportedMetrics()) {
                    if (metric == Metrics.ALL) continue;
                    forest.acceptVisitor(metric.getVisitor(getView()));
                }
            } else {
                forest.acceptVisitor(getView().getMetrics().getVisitor(getView()));
            }

            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.err.printf("Metric calculation: %d ms\n", elapsedTime);
        }
    }

}
