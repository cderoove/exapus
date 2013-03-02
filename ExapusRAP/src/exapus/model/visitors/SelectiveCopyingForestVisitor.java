package exapus.model.visitors;

import exapus.model.forest.ForestElement;
import exapus.model.forest.InboundRef;
import exapus.model.forest.Member;
import exapus.model.forest.OutboundRef;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.Ref;
import exapus.model.view.Selection;

public class SelectiveCopyingForestVisitor extends CopyingForestVisitor implements ICopyingForestVisitor {

	protected Iterable<Selection> selections;	
	protected Iterable<Selection> dual_selections;

	public SelectiveCopyingForestVisitor(Iterable<Selection> selections, Iterable<Selection> dual_selections) {
		super();
		this.selections = selections;
		this.dual_selections = dual_selections;
	}
	
	private void applySelection(ForestElement element, Selection selection) {
		ForestElement copy = forestCopy.getCorrespondingForestElement(true, element);
		copy.copyTagsFrom(element);
		if(selection.hasTag())
			copy.addTag(selection.getTagString());
	}
		
	private boolean visitForestElement(final ForestElement element) {
		boolean selected = false;
		for(Selection selection : selections) {
			if(selection.matchForestElement(element)) {
				selected = true;
				applySelection(element, selection);
			}
		}
		return selected;
	}
	
	@Override
	public boolean visitPackageTree(final PackageTree packageTree) {
		return visitForestElement(packageTree);
	}
	
	@Override
	public boolean visitPackageLayer(final PackageLayer packageLayer) {
		return visitForestElement(packageLayer);		
	}
	
	@Override
	public boolean visitMember(final Member member) {
		return visitForestElement(member);	
	}

	
	private boolean visitReference(final Ref ref) {
		boolean selected = false;
		for(Selection selection : selections) {
			if(selection.match(ref)) {
				for(Selection dual_selection : dual_selections) {
					Ref dual = ref.getDual();
					if(dual_selection.match(dual)) {
						 //TODO: what about dual tags?
						 applySelection(ref,selection);
						 selected = true;
						 break;
					}
				}
			}
		}
		return selected;
	}					


	@Override
	public boolean visitInboundReference(final InboundRef inboundRef) {
		return visitReference(inboundRef);
	}

	@Override
	public boolean visitOutboundReference(OutboundRef outboundRef) {
		return visitReference(outboundRef);
	}					
	
					

					

}

