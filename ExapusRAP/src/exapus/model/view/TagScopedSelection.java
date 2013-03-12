package exapus.model.view;

import exapus.model.forest.FactForest;
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
		setQName(name);
	}
	
	@Override
	public boolean matches(PackageTree packageTree, FactForest sourceForest) {
		return sourceForest.hasTag(packageTree, nameAsTag);
	}
	
	@Override
	public boolean matches(PackageLayer packageLayer, FactForest sourceForest) {
		return sourceForest.hasTag(packageLayer, nameAsTag);
	}

	@Override
	public boolean matches(Member member, FactForest sourceForest) {
		return sourceForest.hasTag(member, nameAsTag);
	}
	
	@Override
	public boolean matches(Ref ref, FactForest sourceForest) {
		return sourceForest.hasTag(ref, nameAsTag);
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

	@Override
	public void setQName(QName name) {
		super.setQName(name);
		nameAsTag = new Tag(name.getIdentifier());
	}
	




	

}
