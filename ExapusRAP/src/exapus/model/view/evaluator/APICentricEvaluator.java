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

			Iterable<Selection> selections = getView().getAPISelections();

			@Override
			protected boolean select(InboundFactForest forest) {
				return true;
			}

			@Override
			protected boolean select(OutboundFactForest forest) {
				return false;
			}

			@Override
			protected boolean select(final PackageTree packageTree) {			
				return Iterables.any(selections, new Predicate<Selection>() {
					@Override
					public boolean apply(Selection selection) {
						return selection.matchAPIPackageTree(packageTree);

					}
				});
			}

			@Override
			protected boolean select(final PackageLayer packageLayer) {
				return Iterables.any(selections, new Predicate<Selection>() {
					@Override
					public boolean apply(Selection selection) {
						return selection.matchAPIPackageLayer(packageLayer);
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
			public boolean visitInboundReference(final InboundRef inboundRef) {
				//loop in regular manner over selections, 
				//every tagged one has to be added to the pktree named that manner
				//idea: gather ancestors in list, traverse it upside-down to add copies to pktree 
			
				//inner any can stay, tags are not supported in other one
				
				for(Selection selection : selections) {
					//have to match all selection as they can each copy the ref to a different packagetree (Todo: optimize by checking first whether there are selections with tag)
					if(selection.matchAPIRef(inboundRef) 
							&& Iterables.any(getView().getProjectSelections(),
									new Predicate<Selection>() {
								@Override
								public boolean apply(Selection selection) {
									return selection.matchProjectRef((OutboundRef)inboundRef.getDual());
								}
							})) {
						if(selection.hasTag()) {
							FactForest forestCopy = getCopy();
							PackageTree tagTree = forestCopy.getOrAddPackageTree(new UqName(selection.getTagString()));
							tagTree.insertReferenceCopy(inboundRef);
							copyInboundReference(inboundRef);
							return false;
							
						} else {
							copyInboundReference(inboundRef);
							return true;
						}
					}
				}
				return false;
			}
			

			
			
			@Override
			protected boolean select(final InboundRef inboundRef) {
				return true;
			}

			@Override
			protected boolean select(OutboundRef outboundRef) {
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
        calculateMetrics(forest);
		modelResult.setAPICentricForest((InboundFactForest) forest);
	}

}
