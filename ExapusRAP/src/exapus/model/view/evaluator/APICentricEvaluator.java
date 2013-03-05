package exapus.model.view.evaluator;

import exapus.model.forest.FactForest;
import exapus.model.forest.InboundFactForest;
import exapus.model.store.Store;
import exapus.model.view.View;
import exapus.model.visitors.FastSelectiveCopyingForestVisitor;
import exapus.model.visitors.ICopyingForestVisitor;
import exapus.model.visitors.SelectiveCopyingForestVisitor;

public class APICentricEvaluator extends Evaluator {
	
	protected APICentricEvaluator(View v) {
		super(v);
	}


	@Override
	protected ICopyingForestVisitor newVisitor() {
		return new FastSelectiveCopyingForestVisitor(getView().getAPISelections(), getView().getProjectSelections());
	}

	@Override
	protected FactForest getCompleteForest() {
		return Store.getCurrent().getWorkspaceModel().getAPICentricForest();
	}


	@Override
	protected void cleanResult() {
		result = new InboundFactForest(null);
	}

}
