package exapus.model.view;

import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.QName;

public class ScopedSelection extends Selection {

	public ScopedSelection(QName name, Scope scope) {
		super();
		this.name = name;
		this.scope = scope;
	}
	
	public ScopedSelection(QName name) {
		this(name, Scope.ROOT_SCOPE);
	}
	

	private Scope scope;
		
	private QName name;
	
	
	private boolean matchPackageTree(PackageTree packageTree) {
		if(scope.equals(Scope.ROOT_SCOPE)) {
			 return packageTree.getQName().equals(name);
		}
		return false;
	}
	
	@Override
	public boolean matchProjectPackageTree(PackageTree packageTree) {
		return matchPackageTree(packageTree);
	}

	@Override
	public boolean matchAPIPackageTree(PackageTree packageTree) {
		return matchPackageTree(packageTree);
	}

	@Override
	public boolean matchAPIPackageLayer(PackageLayer packageLayer) {
		return matchPackageLayer(packageLayer);
	}
	
	@Override
	public boolean matchProjectPackageLayer(PackageLayer packageLayer) {
		return matchPackageLayer(packageLayer);
	}

	private boolean matchPackageLayer(PackageLayer packageLayer) {
		if(scope.equals(Scope.ROOT_SCOPE)) {
			PackageTree tree = packageLayer.getParentPackageTree();
			return matchPackageTree(tree);
		}
		return false;
	}

	@Override
	public boolean matchAPIMember(Member member) {
		return matchMember(member);
	}

	@Override
	public boolean matchProjectMember(Member member) {
		return matchMember(member);
	}

	private boolean matchMember(Member member) {
		if(scope.equals(Scope.ROOT_SCOPE)) {
			PackageTree tree = member.getParentPackageTree();
			return matchPackageTree(tree);
		}
		return false;
	}


	private Scope getScope() {
		return scope;
	}

	private void setScope(Scope scope) {
		this.scope = scope;
	}

	private QName getName() {
		return name;
	}

	private void setName(QName name) {
		this.name = name;
	}

	@Override
	public String getNameString() {
		return getName().toString();
	}

	@Override
	public String getScopeString() {
		return getScope().toString();
	}
	
	
	
}
