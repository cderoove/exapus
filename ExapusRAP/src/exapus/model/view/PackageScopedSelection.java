package exapus.model.view;

import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.Ref;

public class PackageScopedSelection extends ScopedSelection {
	
	@Override
	public boolean matches(PackageLayer packageLayer) {
		return packageLayer.getQName().equals(name);
	}
	
	@Override
	public boolean matches(Member member) {
		return matches(member.getParentPackageLayer());
	}
	
	@Override
	public boolean matches(Ref ref) {
		return matches(ref.getParentPackageLayer());
	}

	@Override
	public boolean mayContainMatches(PackageLayer packageLayer) {
		return packageLayer.getQName().isPrefixOf(name);	
	}
	
	@Override
	public boolean mayContainMatches(Member member) {
		return member.getParentPackageLayer().getQName().equals(name);
	}
	

}
