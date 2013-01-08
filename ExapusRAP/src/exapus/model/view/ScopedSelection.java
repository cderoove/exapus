package exapus.model.view;

import exapus.model.forest.InboundRef;
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
		this(name, Scope.PREFIX_SCOPE);
	}
	

	private Scope scope;
		
	private QName name;
	
	
	//to avoid recomputation for type/method scopes, could go into QName but there it would consume bytes
	//should move to type/method scopes when refactored into a scope hierarchy 
	private QName methodPackageName;
	private QName typePackageName;
	
	private QName getMethodPackageName() {
		if(methodPackageName == null) {
			methodPackageName = getTypePackageName().getButLast();
		}
		return methodPackageName;
	}
	
	private QName getTypePackageName() {
		if(typePackageName == null) {
			typePackageName = name.getButLast();
		}
		return typePackageName;
	}
	
	@Override
	public boolean matchProjectPackageTree(PackageTree packageTree) {
		//projects correspond to package trees, this is the only place where ROOT_SCOPE makes sense
		if(scope.equals(Scope.ROOT_SCOPE)) 
			return packageTree.getQName().equals(name);
		return true;
	}

	@Override
	public boolean matchAPIPackageTree(PackageTree packageTree) {
		//package layers are grouped in one dummy packagetree
		return true;
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
		//multiple scopes can be selected at the same time, cannot trust filtering to have happened above
		if(scope.equals(Scope.ROOT_SCOPE)) 
			return packageLayer.getParentPackageTree().getQName().equals(name);
		
		//scope java.lang, include parent layer java to preserve hierarchy .. filter later on 
		if(scope.equals(Scope.PACKAGE_SCOPE)) 
			return packageLayer.getQName().isPrefixOf(name);
		
		//scope java.lang, include parent layer java to preserve hierarchy .. filter later on 
		if(scope.equals(Scope.PREFIX_SCOPE)) 
			return packageLayer.getQName().isPrefixOf(name) || name.isPrefixOf(packageLayer.getQName());
		
		if(scope.equals(Scope.TYPE_SCOPE)) 
			return packageLayer.getQName().isPrefixOf(getTypePackageName());
			
		if(scope.equals(Scope.METHOD_SCOPE)) 
			return packageLayer.getQName().isPrefixOf(getMethodPackageName());
		
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
		if(scope.equals(Scope.ROOT_SCOPE)) 
			return member.getParentPackageTree().getQName().equals(name);
		
		if(scope.equals(Scope.PACKAGE_SCOPE))
			return member.getParentPackageLayer().getQName().equals(name);
	
		if(scope.equals(Scope.PREFIX_SCOPE)) 
			return name.isPrefixOf(member.getQName());
		
		//methods/fields/inner classes are also members, should be included
		if(scope.equals(Scope.TYPE_SCOPE))
			return name.isPrefixOf(member.getQName());
		
		if(scope.equals(Scope.METHOD_SCOPE)) 
			//prefix parent members have to be included to preserve hierarchy
			//unwanted inner members will have to be filtered out at the reference level
			return getTypePackageName().isPrefixOf(member.getQName());
			
		return false;
	}

	//do the actual work here, other methods only attempt to filter out unwanted elements beforehand
	@Override
	public boolean matchAPIRef(InboundRef inboundRef) {
		if(scope.equals(Scope.ROOT_SCOPE)) 
			return inboundRef.getParentPackageTree().getQName().equals(name);
	
		if(scope.equals(Scope.PACKAGE_SCOPE))
			return inboundRef.getParentPackageLayer().getQName().equals(name);
		
		if(scope.equals(Scope.PREFIX_SCOPE)) 
			return name.isPrefixOf(inboundRef.getQName());
		
		if(scope.equals(Scope.TYPE_SCOPE))
			return name.isPrefixOf(inboundRef.getQName());
		
		if(scope.equals(Scope.METHOD_SCOPE)) 
			return name.isPrefixOf(inboundRef.getQName());
		
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
