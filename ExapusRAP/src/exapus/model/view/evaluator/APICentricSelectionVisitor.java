package exapus.model.view.evaluator;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import exapus.model.forest.InboundFactForest;
import exapus.model.forest.InboundRef;
import exapus.model.forest.OutboundFactForest;
import exapus.model.forest.OutboundRef;
import exapus.model.view.Selection;

public class APICentricSelectionVisitor extends SelectionVisitor {

	public APICentricSelectionVisitor(Iterable<Selection> apis, Iterable<Selection> projects) {
		super(apis, projects);
	}
	
	@Override
	protected boolean select(InboundFactForest forest) {
		return true;
	}

	@Override
	protected boolean select(OutboundFactForest forest) {
		return false;
	}
	

	@Override
	protected boolean select(OutboundRef outboundRef) {
		return false;
	}

	@Override
	protected boolean select(final InboundRef inboundRef) {
		return Iterables.any(selections, new Predicate<Selection>() {
			@Override
			public boolean apply(Selection selection) {
				return selection.matchRef(inboundRef);
			}
		}) && Iterables.any(dual_selections,
				new Predicate<Selection>() {
			@Override
			public boolean apply(Selection selection) {
				return selection.matchRef(inboundRef.getDual());
			}
		});
	}


	

}
