package exapus.model.view.evaluator;

import exapus.model.forest.FactForest;
import exapus.model.metrics.MetricType;
import exapus.model.stats.StatsCollectionVisitor;
import exapus.model.tags.TagsPropagationVisitor;
import exapus.model.view.View;
import exapus.model.visitors.ICopyingForestVisitor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Evaluator {

	private View view;

	protected FactForest result;

	protected abstract void cleanResult();

    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private Date date = new Date();

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
        System.err.printf("%s\tEvaluating view \"%s\" (%s)\n",
                currentTimestamp(), getView().getName(), getView().isAPICentric() ? "API-centric" : "Project-centric");
        long startTime = System.currentTimeMillis();

        FactForest forest = fetchForest();
		calculateMetrics(forest);
        propagateTags(forest);
		result = forest;

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.err.printf("%s\tSpent on \"%s\" (%s): %d s\n",
                currentTimestamp(), getView().getName(), getView().isAPICentric() ? "API-centric" : "Project-centric", elapsedTime / 1000);
    }

    private FactForest fetchForest() {
        long startTime = System.currentTimeMillis();

        ICopyingForestVisitor v = newVisitor();
        FactForest dualForest = getDualSourceForest();
        v.setDualForest(dualForest);
        FactForest sourceForest = getSourceForest();
        FactForest copy = v.copy(sourceForest);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.err.printf("\t%s\tFetching the forest: %d ms\n", currentTimestamp(), elapsedTime);

        return copy;
    }

    private void propagateTags(FactForest forest) {
        long startTime = System.currentTimeMillis();

        forest.acceptVisitor(new TagsPropagationVisitor(getView()));

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.err.printf("\t%s\tTags propagation: %d ms\n", currentTimestamp(), elapsedTime);
    }

    protected abstract ICopyingForestVisitor newVisitor();

	protected abstract FactForest getCompleteForest();

	protected abstract FactForest getDualCompleteForest();

	protected FactForest getSourceForest() {
		View sourceView= getView().getSourceView();
		if(sourceView != null)
			return sourceView.evaluate();
		return getCompleteForest();
	}

	protected FactForest getDualSourceForest() {
		View sourceView= getView().getDualSourceView();
		if(sourceView != null)
			return sourceView.evaluate();
		return getDualCompleteForest();
	}

	protected void calculateMetrics(FactForest forest) {
		calculateMetrics(getView(), forest);
	}

    protected void calculateMetrics(View view, FactForest forest) {
    	MetricType type = view.getMetricType();
        if (type != null) {
            long startTime = System.currentTimeMillis();

            if (type == MetricType.ALL) {
                for (MetricType metric : MetricType.supportedMetrics(view.getRenderable(), view.isAPICentric())) {
                    if (metric == MetricType.ALL) continue;
                    forest.acceptVisitor(metric.getVisitor(view));
                }
            } else {
                forest.acceptVisitor(type.getVisitor(view));
            }

            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.err.printf("\t%s\tMetric calculation: %d ms\n", currentTimestamp(), elapsedTime);

            calculateStats(view, forest);
        }
    }

    private void calculateStats(View view, FactForest forest) {
        long startTime = System.currentTimeMillis();

        forest.acceptVisitor(new StatsCollectionVisitor(view));

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.err.printf("\t%s\tStats calculation: %d ms\n", currentTimestamp(), elapsedTime);
    }

    private String currentTimestamp() {
        date = new Date();
        return dateFormat.format(date);
    }

}
