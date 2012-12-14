package exapus.model.view.evaluator;

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
import exapus.model.visitors.IForestVisitor;
import exapus.model.visitors.SelectiveCopyingForestVisitor;

public class APICentricEvaluator extends Evaluator {
	
	protected APICentricEvaluator(View v) {
		super(v);
	}

	@Override
	public FactForest getResult() {
		return modelResult.getAPICentricForest();
	}


	protected SelectiveCopyingForestVisitor newVisitor() {
		SelectiveCopyingForestVisitor visitor = new SelectiveCopyingForestVisitor() {
			
			Selection APISelection = getView().getAPISelection();

			@Override
			protected boolean select(InboundFactForest forest) {
				return true;
			}

			@Override
			protected boolean select(OutboundFactForest forest) {
				return false;
			}

			@Override
			protected boolean select(PackageTree packageTree) {
				return APISelection.matchAPIPackageTree(packageTree);
			}

			@Override
			protected boolean select(PackageLayer packageLayer) {
				return APISelection.matchAPIPackageLayer(packageLayer);
			}

			@Override
			protected boolean select(Member member) {
				return APISelection.matchAPIMember(member);
			}

			@Override
			protected boolean select(InboundRef inboundRef) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			protected boolean select(OutboundRef outboundRef) {
				// TODO Auto-generated method stub
				return false;
			}
			
		};
		return visitor;
	}

	@Override
	public void evaluate() {
		SelectiveCopyingForestVisitor v = newVisitor();
		InboundFactForest workspaceForest = Store.getCurrent().getWorkspaceModel().getAPICentricForest();
		FactForest forest = v.copy(workspaceForest);
		modelResult.setAPICentricForest((InboundFactForest) forest);
	}

}
