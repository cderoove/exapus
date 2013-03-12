package exapus.model.view;

import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.Ref;

public class TypeScopedSelection extends ScopedSelection {
	
	@Override
	public boolean matches(PackageLayer packageLayer) {
		return false;
	}
	
	@Override
	public boolean matches(Member member) {
		return name.isPrefixOf(member.getQName());	
	}
	
	@Override
	public boolean matches(Ref ref) {
		return name.isPrefixOf(ref.getQName());
	}
	
	@Override
	public boolean mayContainMatches(PackageLayer packageLayer) {
		return packageLayer.getQName().isPrefixOf(getTypePackageName());
	}
	
	@Override
	public boolean mayContainMatches(Member member) {
		return name.isPrefixOf(member.getQName());
	}



}
