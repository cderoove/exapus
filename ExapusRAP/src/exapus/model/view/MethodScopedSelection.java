package exapus.model.view;

import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.Ref;

public class MethodScopedSelection extends ScopedSelection {

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
		return packageLayer.getQName().isPrefixOf(getMethodPackageName());
	}
	
	@Override
	public boolean mayContainMatches(Member member) {
			//prefix parent members have to be included to preserve hierarchy
			//unwanted inner members will have to be filtered out at the reference level
		return getTypePackageName().isPrefixOf(member.getQName());
	}

		
		

}
