package exapus.model.visitors;

import java.util.Iterator;

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

	//for now, visit always continues up to the reference level
	private boolean mayContainMatchingReferences(ForestElement e) {
		return true;
	}

	@Override
	public boolean visitPackageTree(final PackageTree packageTree) {	
		visitForestElement(packageTree);
		return mayContainMatchingReferences(packageTree);
	}


	@Override
	public boolean visitPackageLayer(final PackageLayer packageLayer) {
		visitForestElement(packageLayer);
		return mayContainMatchingReferences(packageLayer);		
	}

	@Override
	public boolean visitMember(final Member member) {
		visitForestElement(member);
		return mayContainMatchingReferences(member);	
	}

	private boolean visitReference(final Ref ref) {
		for(Selection selection : selections) {
			if(selection.matches(ref)) {
				for(Selection dual_selection : dual_selections) {
					Ref dual = ref.getDual();
					//ref has to match one of the selections
					if(dual_selection.matches(dual)) {
						Ref copy = (Ref) forestCopy.getCorrespondingForestElement(true, ref);
						//copy.copyTagsFrom(ref);
						//then iteratively apply tags to copy of ref
						return applyTags(copy);
					}
				}
			}
		}
		return false;
	}

		
	private boolean applyTags(ForestElement copy) {
		Iterator<Selection> i = selections.iterator();
		while(i.hasNext()) {
			Selection selection =  i.next();
			if(selection.hasTag()) {
				if(selection.matches(copy)) {
					if(copy.addTag(selection.getTag()))
						//re-iterate when a new tag has been added
						i = selections.iterator(); 
				}
			}
		}
		return true;
	}
	
	private void visitForestElement(final ForestElement element) {
		for(Selection selection : selections) {
			if(selection.matches(element)) { 
				ForestElement copy = forestCopy.getCorrespondingForestElement(true,element);
				//copy.copyTagsFrom(element);
				applyTags(copy);
				return;
			}
		}
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

