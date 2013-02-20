package exapus.gui.editors.view.definition;

import com.google.common.collect.Iterables;

import exapus.gui.editors.forest.tree.ForestTreeContentProvider;
import exapus.model.forest.Member;
import exapus.model.forest.MemberContainer;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.Ref;

public class ScopeSelectionForestTreeContentProvider extends ForestTreeContentProvider {

	public ScopeSelectionForestTreeContentProvider() {
	}
	
	@Override
	public  Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Member) {
			Member member = (Member) parentElement;
			return Iterables.toArray(member.getMembers(), Object.class);
		} else
			return super.getChildren(parentElement);
	}


}
