package exapus.model.view;

import java.util.List;

import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.UqName;

public class ScopedSelection extends Selection {

	public ScopedSelection(UqName name, Scope scope) {
		super();
		this.name = name;
		this.scope = scope;
	}
	
	public ScopedSelection(UqName name) {
		this(name,RootScope.getCurrent());
	}
	

	private Scope scope;
		
	private UqName name;


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

	private UqName getName() {
		return name;
	}

	private void setName(UqName name) {
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
