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


	@Override
	public boolean matchAPIPackageTree(PackageTree packageTree) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean matchAPIPackageLayer(PackageLayer packageLayer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean matchAPIMember(Member member) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean matchProjectPackageTree(PackageTree packageTree) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean matchProjectPackageLayer(PackageLayer packageLayer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean matchProjectMember(Member member) {
		// TODO Auto-generated method stub
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
