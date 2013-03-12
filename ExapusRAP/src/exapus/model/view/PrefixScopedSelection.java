package exapus.model.view;

import exapus.model.forest.FactForest;
import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.Ref;

public class PrefixScopedSelection extends ScopedSelection {
	
	@Override
	public boolean matches(PackageLayer packageLayer, FactForest source) {
		return name.isPrefixOf(packageLayer.getQName());
	}
	
	@Override
	public boolean matches(Member member, FactForest source) {
		return name.isPrefixOf(member.getQName());
	}
	
	@Override
	public boolean matches(Ref ref, FactForest source) {
		return name.isPrefixOf(ref.getQName());
	}
	
	@Override
	public boolean mayContainMatches(PackageLayer packageLayer) {
		return packageLayer.getQName().isPrefixOf(name) || name.isPrefixOf(packageLayer.getQName());
	}

	@Override
	public boolean mayContainMatches(Member member) {
		return name.isPrefixOf(member.getQName());
	}

	


}
