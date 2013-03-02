package exapus.model.view.evaluator;

import exapus.model.forest.FactForest;
import exapus.model.forest.InboundFactForest;
import exapus.model.forest.OutboundFactForest;
import exapus.model.store.Store;
import exapus.model.view.View;
import exapus.model.visitors.ICopyingForestVisitor;
import exapus.model.visitors.IForestVisitor;
import exapus.model.visitors.SelectiveCopyingForestVisitor;

public class ProjectCentricEvaluator extends Evaluator {

	protected ProjectCentricEvaluator(View v) {
		super(v);
	}

	@Override
	protected ICopyingForestVisitor newVisitor() {
		return new SelectiveCopyingForestVisitor(getView().getProjectSelections(), getView().getAPISelections());
	}

	@Override
	protected FactForest getCompleteForest() {
		return Store.getCurrent().getWorkspaceModel().getProjectCentricForest();
	}

	@Override
	protected void cleanResult() {
		result = new OutboundFactForest(null);
	}

}
