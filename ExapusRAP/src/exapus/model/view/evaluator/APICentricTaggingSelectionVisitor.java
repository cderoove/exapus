package exapus.model.view.evaluator;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import exapus.model.forest.FactForest;
import exapus.model.forest.InboundRef;
import exapus.model.forest.OutboundRef;
import exapus.model.forest.PackageTree;
import exapus.model.forest.UqName;
import exapus.model.view.Selection;

public class APICentricTaggingSelectionVisitor extends APICentricSelectionVisitor {

	public APICentricTaggingSelectionVisitor(Iterable<Selection> apis, Iterable<Selection> projects) {
		super(apis, projects);
	}
	
	@Override
	public boolean visitInboundReference(final InboundRef inboundRef) {
		for(Selection selection : selections) {
			//have to match all selection as they can each copy the ref to a different packagetree
			if(selection.matchRef(inboundRef) 
					&& Iterables.any(dual_selections, new Predicate<Selection>() {
						@Override
						public boolean apply(Selection selection) {
							return selection.matchRef(inboundRef.getDual());
						}
					})) {
				if(selection.hasTag()) {
					FactForest forestCopy = getCopy();
					PackageTree tagTree = forestCopy.getOrAddPackageTree(new UqName(selection.getTagString()));
					tagTree.copyReference(inboundRef);
					//enable if we want to keep a copy in the original location
					//copyInboundReference(inboundRef); 
					return false;
				} else {
					copyInboundReference(inboundRef);
					return true;
				}
			}
		}
		return false;
	}


}
