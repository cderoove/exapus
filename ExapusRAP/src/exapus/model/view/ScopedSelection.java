package exapus.model.view;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import exapus.model.forest.Direction;
import exapus.model.forest.ForestElement;
import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.QName;
import exapus.model.forest.Ref;
import exapus.model.tags.Tag;

@XmlRootElement
public class ScopedSelection extends Selection {

	public ScopedSelection() {
		//only to be used by JAXB
	}
	
	public ScopedSelection(QName name, Scope scope, Tag tag) {
		super();
		this.name = name;
		this.scope = scope;
		this.tag = tag;
		if(scope == Scope.TAG_SCOPE)
			this.nameAsTag = new Tag(name.getIdentifier());
	}
	
	public ScopedSelection(QName name, Scope scope) {
		this(name,scope,null);
	}

	
	public ScopedSelection(QName name) {
		this(name, Scope.PREFIX_SCOPE);
	}
	
	private Scope scope;
	
	private QName name;
	
	private Tag tag;
	
	
	@XmlElement
	public Tag getTag() {
		return tag;
	}
	
	public void setTag(Tag tag) {
		this.tag = tag;
	}
	
	public boolean hasTag() {
		return tag != null;
	}
	
	private Tag nameAsTag;

	
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
	public boolean matches(PackageTree packageTree) {
		if(scope.equals(Scope.TAG_SCOPE))
			return packageTree.hasTag(nameAsTag);

		if(scope.equals(Scope.ROOT_SCOPE)) 
			return packageTree.getQName().equals(name);
		return false;
	}
	
	@Override
	public boolean matches(PackageLayer packageLayer) {
		if(scope.equals(Scope.TAG_SCOPE))
			return packageLayer.hasTag(nameAsTag);

		if(scope.equals(Scope.ROOT_SCOPE)) 
			return matches(packageLayer.getParentPackageTree());
		
		if(scope.equals(Scope.PACKAGE_SCOPE)) 
			return packageLayer.getQName().equals(name);
		
		if(scope.equals(Scope.PREFIX_SCOPE)) 
			return name.isPrefixOf(packageLayer.getQName());
		
		if(scope.equals(Scope.TYPE_SCOPE))
			return false;
		
		if(scope.equals(Scope.METHOD_SCOPE)) 
			return false;

		return false;
	}

	@Override
	public boolean matches(Member member) {
		if(scope.equals(Scope.TAG_SCOPE))
			return member.hasTag(nameAsTag);

		if(scope.equals(Scope.ROOT_SCOPE)) 
			return matches(member.getParentPackageTree());
		
		if(scope.equals(Scope.PACKAGE_SCOPE)) 
			return matches(member.getParentPackageLayer());
		
		if(scope.equals(Scope.PREFIX_SCOPE)) 
			return name.isPrefixOf(member.getQName());
		
		if(scope.equals(Scope.TYPE_SCOPE))
			return name.isPrefixOf(member.getQName());
		
		if(scope.equals(Scope.METHOD_SCOPE)) 
			return name.isPrefixOf(member.getQName());

		return false;
	}
	
	@Override
	public boolean matches(Ref ref) {
		if(scope.equals(Scope.TAG_SCOPE))
			return ref.hasTag(nameAsTag);

		if(scope.equals(Scope.ROOT_SCOPE)) 
			return ref.getParentPackageTree().getQName().equals(name);
	
		if(scope.equals(Scope.PACKAGE_SCOPE))
			return ref.getParentPackageLayer().getQName().equals(name);
		
		if(scope.equals(Scope.PREFIX_SCOPE)) 
			return name.isPrefixOf(ref.getQName());
		
		if(scope.equals(Scope.TYPE_SCOPE))
			return name.isPrefixOf(ref.getQName());
		
		if(scope.equals(Scope.METHOD_SCOPE)) 
			return name.isPrefixOf(ref.getQName());
		
		return false;
	}


	
	
	
	@Override
	public boolean mayContainMatches(PackageLayer packageLayer) {
		if(scope.equals(Scope.TAG_SCOPE))
			return true;
		
		if(scope.equals(Scope.ROOT_SCOPE)) 
			return packageLayer.getParentPackageTree().getQName().equals(name);
		
		if(scope.equals(Scope.PACKAGE_SCOPE)) 
			return packageLayer.getQName().isPrefixOf(name);
		
		if(scope.equals(Scope.PREFIX_SCOPE)) 
			return packageLayer.getQName().isPrefixOf(name) || name.isPrefixOf(packageLayer.getQName());
		
		if(scope.equals(Scope.TYPE_SCOPE)) 
			return packageLayer.getQName().isPrefixOf(getTypePackageName());
			
		if(scope.equals(Scope.METHOD_SCOPE)) 
			return packageLayer.getQName().isPrefixOf(getMethodPackageName());
		
		return false;
	}

	@Override
	public boolean mayContainMatches(Member member) {
		if(scope.equals(Scope.TAG_SCOPE))
			return true;

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
	
	@Override
	public boolean mayContainMatches(PackageTree packageTree) {
		if(scope.equals(Scope.TAG_SCOPE))
			return true;

		if(scope.equals(Scope.ROOT_SCOPE)) 
			return packageTree.getQName().equals(name);
			
		return false;
	}
	
		
	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	@XmlElement
	public QName getQName() {
		return name;
	}

	private void setQName(QName name) {
		this.name = name;
	}

	@Override
	public String getNameString() {
		return getQName().toString();
	}

	@Override
	public String getScopeString() {
		return getScope().toString();
	}



	
	
}
