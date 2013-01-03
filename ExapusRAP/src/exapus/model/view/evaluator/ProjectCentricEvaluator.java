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
import exapus.model.store.Store;
import exapus.model.view.Selection;
import exapus.model.view.View;
import exapus.model.visitors.SelectiveCopyingForestVisitor;

public class ProjectCentricEvaluator extends Evaluator {

	protected ProjectCentricEvaluator(View v) {
		super(v);
	}

	@Override
	public FactForest getResult() {
		return getModelResult().getProjectCentricForest();
	}
	
	protected SelectiveCopyingForestVisitor newVisitor() {
		SelectiveCopyingForestVisitor visitor = new SelectiveCopyingForestVisitor() {
			
			Iterable<Selection> selections = getView().getProjectSelections();

			@Override
			protected boolean select(InboundFactForest forest) {
				return false;
			}

			@Override
			protected boolean select(OutboundFactForest forest) {
				return true;
			}

			@Override
			protected boolean select(final PackageTree packageTree) {
				return Iterables.any(selections, new Predicate<Selection>() {
					@Override
					public boolean apply(Selection selection) {
						return selection.matchProjectPackageTree(packageTree);
					}
				});
			}

			@Override
			protected boolean select(final PackageLayer packageLayer) {
				return Iterables.any(selections, new Predicate<Selection>() {
					@Override
					public boolean apply(Selection selection) {
						return selection.matchProjectPackageLayer(packageLayer);

					}
				});
			}

			@Override
			protected boolean select(final Member member) {
				return Iterables.any(selections, new Predicate<Selection>() {
					@Override
					public boolean apply(Selection selection) {
						return selection.matchAPIMember(member);

					}
				});
			}

			@Override
			protected boolean select(InboundRef inboundRef) {
				return false;
			}

			@Override
			protected boolean select(OutboundRef outboundRef) {
				return true;
			}
			
		};
		return visitor;
	}

	@Override
	public void evaluate() {
		SelectiveCopyingForestVisitor v = newVisitor();
		OutboundFactForest workspaceForest = Store.getCurrent().getWorkspaceModel().getProjectCentricForest();
		FactForest forest = v.copy(workspaceForest);
		modelResult.setProjectCentricForest((OutboundFactForest) forest);		
	}

}
