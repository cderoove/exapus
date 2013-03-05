package exapus.model.view;

import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.QName;
import exapus.model.forest.Ref;
import exapus.model.tags.Tag;

public class TagScopedSelection extends ScopedSelection {
	
	private Tag nameAsTag;

	@Override
	protected void configureFor(Scope scope, QName name, Tag tag) {
		super.configureFor(scope, name, tag);
		nameAsTag = new Tag(name.getIdentifier());
	}
	
	@Override
	public boolean matches(PackageTree packageTree) {
		return packageTree.hasTag(nameAsTag);
	}
	
	@Override
	public boolean matches(PackageLayer packageLayer) {
		return packageLayer.hasTag(nameAsTag);
	}

	@Override
	public boolean matches(Member member) {
		return member.hasTag(nameAsTag);
	}
	
	@Override
	public boolean matches(Ref ref) {
		return ref.hasTag(nameAsTag);
	}
	
	@Override
	public boolean mayContainMatches(PackageLayer packageLayer) {
		return true;		
	}
	
	@Override
	public boolean mayContainMatches(Member member) {
		return true;
	}
	
	@Override
	public boolean mayContainMatches(PackageTree packageTree) {
		return true;
	}



	

}
