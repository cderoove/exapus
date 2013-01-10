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
import exapus.model.view.Selection;
import exapus.model.visitors.SelectiveCopyingForestVisitor;

public abstract class SelectionVisitor extends SelectiveCopyingForestVisitor {
	
	protected Iterable<Selection> selections;	
	protected Iterable<Selection> dual_selections;
	
	public SelectionVisitor(Iterable<Selection> selections, Iterable<Selection> dual_selections) {
		this.selections = selections;
		this.dual_selections = dual_selections;
	}

	@Override
	protected boolean select(final PackageTree packageTree) {			
		return Iterables.any(selections, new Predicate<Selection>() {
			@Override
			public boolean apply(Selection selection) {
				return selection.matchPackageTree(packageTree);

			}
		});
	}

	@Override
	protected boolean select(final PackageLayer packageLayer) {
		return Iterables.any(selections, new Predicate<Selection>() {
			@Override
			public boolean apply(Selection selection) {
				return selection.matchPackageLayer(packageLayer);
			}
		});
	}

	@Override
	protected boolean select(final Member member) {
		return Iterables.any(selections, new Predicate<Selection>() {
			@Override
			public boolean apply(Selection selection) {
				return selection.matchMember(member);

			}
		});
	}
	
	

}