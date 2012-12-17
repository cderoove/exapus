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
			
			Selection ProjectSelection = getView().getProjectSelection();

			@Override
			protected boolean select(InboundFactForest forest) {
				return false;
			}

			@Override
			protected boolean select(OutboundFactForest forest) {
				return true;
			}

			@Override
			protected boolean select(PackageTree packageTree) {
				return ProjectSelection.matchProjectPackageTree(packageTree);
			}

			@Override
			protected boolean select(PackageLayer packageLayer) {
				return ProjectSelection.matchProjectPackageLayer(packageLayer);
			}

			@Override
			protected boolean select(Member member) {
				return ProjectSelection.matchProjectMember(member);
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
		OutboundFactForest workspaceForest = Store.getCurrent().getWorkspaceModel().getProjectCentricForest();
		FactForest forest = v.copy(workspaceForest);
		modelResult.setProjectCentricForest((OutboundFactForest) forest);		
	}

}
