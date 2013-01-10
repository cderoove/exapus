package exapus.model.view.evaluator;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import exapus.model.forest.InboundFactForest;
import exapus.model.forest.InboundRef;
import exapus.model.forest.OutboundFactForest;
import exapus.model.forest.OutboundRef;
import exapus.model.view.Selection;

public class ProjectCentricSelectionVisitor extends SelectionVisitor {

	public ProjectCentricSelectionVisitor(Iterable<Selection> apis, Iterable<Selection> projects) {
		super(apis, projects);
	}
	
	@Override
	protected boolean select(InboundRef inboundRef) {
		return false;
	}

	@Override
	protected boolean select(InboundFactForest forest) {
		return false;
	}

	@Override
	protected boolean select(OutboundFactForest forest) {
		return true;
	}
	
	@Override
	protected boolean select(final OutboundRef outboundRef) {
		return Iterables.any(selections, new Predicate<Selection>() {
			@Override
			public boolean apply(Selection selection) {
				return selection.matchRef(outboundRef);
			}
		}) && Iterables.any(dual_selections,
				new Predicate<Selection>() {
			@Override
			public boolean apply(Selection selection) {
				return selection.matchRef(outboundRef.getDual());
			}
		});
	}

	

	

}
