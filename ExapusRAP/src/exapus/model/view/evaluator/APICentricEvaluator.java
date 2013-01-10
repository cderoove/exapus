package exapus.model.view.evaluator;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import exapus.model.forest.FactForest;
import exapus.model.forest.InboundFactForest;
import exapus.model.forest.InboundRef;
import exapus.model.forest.Member;
import exapus.model.forest.OutboundFactForest;
import exapus.model.forest.OutboundRef;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.UqName;
import exapus.model.store.Store;
import exapus.model.view.Selection;
import exapus.model.view.View;
import exapus.model.visitors.SelectiveCopyingForestVisitor;
import exapus.model.view.evaluator.APICentricTaggingSelectionVisitor;
import exapus.model.view.evaluator.APICentricSelectionVisitor;

public class APICentricEvaluator extends Evaluator {
	
	protected APICentricEvaluator(View v) {
		super(v);
	}

	@Override
	public FactForest getResult() {
		return modelResult.getAPICentricForest();
	}

	protected SelectiveCopyingForestVisitor newVisitor() {
		if(Iterables.any(getView().getAPISelections(), new Predicate<Selection>() {
			@Override
			public boolean apply(Selection selection) {
				return selection.hasTag();
			}
		}))
			return new APICentricTaggingSelectionVisitor(getView().getAPISelections(), getView().getProjectSelections());
		else
			return new APICentricSelectionVisitor(getView().getAPISelections(), getView().getProjectSelections());
	}
	
	@Override
	public void evaluate() {
		SelectiveCopyingForestVisitor v = newVisitor();
		InboundFactForest workspaceForest = Store.getCurrent().getWorkspaceModel().getAPICentricForest();
		FactForest forest = v.copy(workspaceForest);
        calculateMetrics(forest);
		modelResult.setAPICentricForest((InboundFactForest) forest);
	}

}
