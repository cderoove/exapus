package exapus.model.view.evaluator;

import exapus.model.forest.FactForest;
import exapus.model.forest.OutboundFactForest;
import exapus.model.store.Store;
import exapus.model.view.View;
import exapus.model.visitors.ICopyingForestVisitor;
import exapus.model.visitors.SelectiveTopDownCopyingForestVisitor;

public class ProjectCentricEvaluator extends Evaluator {

	protected ProjectCentricEvaluator(View v) {
		super(v);
	}

	@Override
	public FactForest getResult() {
		return getModelResult().getProjectCentricForest();
	}

	protected ICopyingForestVisitor newVisitor() {
		return new ProjectCentricSelectionVisitor(getView().getProjectSelections(), getView().getAPISelections());
	}

	@Override
	public void evaluate() {
		ICopyingForestVisitor v = newVisitor();
		OutboundFactForest workspaceForest = Store.getCurrent().getWorkspaceModel().getProjectCentricForest();
		FactForest forest = v.copy(workspaceForest);
		calculateMetrics(forest);
		modelResult.setProjectCentricForest((OutboundFactForest) forest);
	}

}
