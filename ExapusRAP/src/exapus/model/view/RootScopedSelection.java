package exapus.model.view;

import exapus.model.forest.FactForest;
import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.Ref;

public class RootScopedSelection extends ScopedSelection {
	
	@Override
	public boolean matches(PackageTree packageTree, FactForest source) {
		return packageTree.getQName().equals(name);
	}
	
	@Override
	public boolean matches(PackageLayer packageLayer, FactForest source) {
		return matches(packageLayer.getParentPackageTree(), source);
	}
	
	@Override
	public boolean matches(Member member, FactForest source) {
		return matches(member.getParentPackageTree(), source);
	}

	@Override
	public boolean matches(Ref ref, FactForest source) {
		return matches(ref.getParentPackageTree(), source);
	}
	
	@Override
	public boolean mayContainMatches(PackageLayer packageLayer) {
		return mayContainMatches(packageLayer.getParentPackageTree());
	}
	
	@Override
	public boolean mayContainMatches(Member member) {
		return mayContainMatches(member.getParentPackageTree());
	}
	
	@Override
	public boolean mayContainMatches(PackageTree packageTree) {
		return packageTree.getQName().equals(name);
	}





}
