package exapus.model.visitors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Iterables;

import exapus.model.forest.FactForest;
import exapus.model.forest.ForestElement;
import exapus.model.forest.ILayerContainer;
import exapus.model.forest.InboundRef;
import exapus.model.forest.Member;
import exapus.model.forest.MemberContainer;
import exapus.model.forest.OutboundRef;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.Ref;
import exapus.model.view.Selection;

public class FastSelectiveCopyingForestVisitor extends CopyingForestVisitor implements ICopyingForestVisitor {

	protected Iterable<Selection> selections;	
	protected Iterable<Selection> dual_selections;

	
	public FastSelectiveCopyingForestVisitor(Iterable<Selection> selections, Iterable<Selection> dual_selections) {
		super();
		this.selections = selections;
		this.dual_selections = dual_selections;
	}

	protected void registerCopy(ForestElement original, ForestElement copy) {
		copies.put(original, copy);
	}

	protected ForestElement getCopy(ForestElement original) {
		return copies.get(original);
	}

	protected boolean hasCopy(ForestElement original) {
		return copies.containsKey(original);
	}

	private Map<ForestElement,ForestElement> copies;


	@Override
	protected void clear() {
		super.clear();
		copies = new HashMap<ForestElement,ForestElement>();
	}


	//begin code duplication to avoid instanceof checks in selection.matches

	protected boolean anySelectionMatches(Ref ref) {
		for(Selection selection : selections) 
			if(selection.matches(ref,sourceForest))
				return true;
		return false;
	}

	protected boolean mayContainMatches(PackageTree tree) {
		for(Selection selection : selections) 
			if(selection.mayContainMatches(tree))
				return true;
		return false;
	}



	protected boolean mayContainMatches(PackageLayer layer) {
		for(Selection selection : selections) 
			if(selection.mayContainMatches(layer))
				return true;
		return false;
	}

	protected boolean mayContainMatches(Member member) {
		for(Selection selection : selections) 
			if(selection.mayContainMatches(member))
				return true;
		return false;
	}

	private boolean applyTags(PackageTree copy, FactForest sourceForest, PackageTree original) {
		for(Selection selection : selections) {
			if(selection.hasTag()) {
				if(selection.matches(original,sourceForest)) {
					forestCopy.addTag(copy, selection.getTag());
				}
			}
		}
		return true;
	}


	private boolean applyTags(PackageLayer copy, FactForest sourceForest, PackageLayer original) {
		for(Selection selection : selections) {
			if(selection.hasTag()) {
				if(selection.matches(original,sourceForest)) {
					forestCopy.addTag(copy, selection.getTag());
				}
			}
		}
		return true;
	}

	private boolean applyTags(Member copy, FactForest sourceForest, Member original) {
		for(Selection selection : selections) {
			if(selection.hasTag()) {
				if(selection.matches(original,sourceForest)) {
					forestCopy.addTag(copy, selection.getTag());
				}
			}
		}
		return true;
	}
	
	
	private boolean applyTags(Ref copy, FactForest sourceForest, Ref original) {
		for(Selection selection : selections) {
			if(selection.hasTag()) {
				if(selection.matches(original,sourceForest)) {
					forestCopy.addTag(copy, selection.getTag());
				}
			}
		}
		return true;
	}
	//end code duplication

	private boolean applyDualTags(Ref copy, FactForest sourceForest, Ref original, FactForest dualForest, Ref dual) {
		for(Selection selection : dual_selections) {
			if(selection.hasTag()) {
				if(selection.matches(dual,dualForest)) {
					forestCopy.addDualTag(copy, selection.getTag());
				}
			}
		}
		return true;
	}

	

	@Override
	public boolean visitPackageTree(final PackageTree tree) {
		if(mayContainMatches(tree)) {
			PackageTree copy = PackageTree.from(tree);
			forestCopy.addPackageTree(copy);
			forestCopy.intializeTagsForFrom(copy, sourceForest, tree);
			registerCopy(tree, copy);
			applyTags(copy, sourceForest, tree); 
			return true;
		} 
		return false;
	}


	@Override
	public boolean visitPackageLayer(final PackageLayer packageLayer) {
		if(mayContainMatches(packageLayer)) {
			ILayerContainer parentCopy = (ILayerContainer) getCopy(packageLayer.getParent());
			PackageLayer copy = PackageLayer.from(packageLayer);	
			parentCopy.addLayer(copy);
			forestCopy.intializeTagsForFrom(copy, sourceForest, packageLayer);
			registerCopy(packageLayer, copy);
			applyTags(copy, sourceForest, packageLayer);
			return true;
		} 
		return false;
	}

	@Override
	public boolean visitMember(final Member member) {
		if(mayContainMatches(member)) {
			MemberContainer parentCopy = (MemberContainer) getCopy(member.getParent());
			Member copy = Member.from(member);	
			parentCopy.addMember(copy);
			forestCopy.intializeTagsForFrom(copy, sourceForest, member);
			registerCopy(member, copy);
			applyTags(copy, sourceForest, member);
			return true;
		}
		return false;
	}

	private boolean visitReference(final Ref ref) {
		if(anySelectionMatches(ref)) {
			//dual resides in workspace forest (All APIs/ All Projects), not in the dualForest
			Ref dual = ref.getDual();
			
			Member parentCopy = (Member) getCopy(ref.getParent());
			for(Selection dual_selection : dual_selections) {
				//ref has to match one of the selections
				if(dual_selection.matches(dual, dualForest)) {
					Member parentCopyAsMember = (Member) parentCopy;
					Ref copy = Ref.from(ref);
					parentCopyAsMember.addAPIReference(copy);
					forestCopy.intializeTagsForFrom(copy, sourceForest, ref);
					applyTags(copy, sourceForest, ref);
					forestCopy.intializeDualTagsForFrom(copy, sourceForest, ref, dualForest, dual); 					
					applyDualTags(copy, sourceForest, ref, dualForest, dual); 		
					return true;
				}
			}
		}
		return false;
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

