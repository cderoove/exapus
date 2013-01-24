package exapus.model.view.evaluator;

import exapus.model.forest.ExapusModel;
import exapus.model.forest.FactForest;
import exapus.model.forest.OutboundFactForest;
import exapus.model.metrics.MetricType;
import exapus.model.stats.StatsCollectionVisitor;
import exapus.model.store.Store;
import exapus.model.view.View;
import exapus.model.visitors.ICopyingForestVisitor;

public abstract class Evaluator {

	private View view;
	
	protected FactForest result;

	protected abstract void cleanResult();
	
	public FactForest getResult() {
		return result;
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
		cleanResult();
	}

	public View getView() {
		return view;
	}

	public void evaluate() {
		ICopyingForestVisitor v = newVisitor();
		FactForest sourceForest = getSourceForest();
		FactForest forest = v.copy(sourceForest);
		calculateMetrics(forest);
		result = forest;
	}
	
	protected abstract ICopyingForestVisitor newVisitor();

	protected abstract FactForest getCompleteForest();
	
	protected FactForest getSourceForest() {
		String sourceViewName = getView().getSourceViewName();
		if(sourceViewName == null)
			return getCompleteForest();
		else
			return Store.getCurrent().getView(sourceViewName).evaluate();
	}
	
    protected void calculateMetrics(FactForest forest) {
        if (getView().getMetricType() != null) {
            long startTime = System.currentTimeMillis();

            if (getView().getMetricType() == MetricType.ALL) {
                for (MetricType metric : MetricType.supportedMetrics(getView().getRenderable())) {
                    if (metric == MetricType.ALL) continue;
                    forest.acceptVisitor(metric.getVisitor(getView()));
                }
            } else {
                forest.acceptVisitor(getView().getMetricType().getVisitor(getView()));
            }

            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.err.printf("Metric calculation: %d ms\n", elapsedTime);

            calculateStats(forest);
        }
    }

    private void calculateStats(FactForest forest) {
        long startTime = System.currentTimeMillis();

        forest.acceptVisitor(new StatsCollectionVisitor(getView()));

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.err.printf("Stats calculation: %d ms\n", elapsedTime);
    }

}
