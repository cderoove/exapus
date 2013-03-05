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

	static public ScopedSelection forScope(QName name) {
		return forScope(Scope.PREFIX_SCOPE, name);
	}
	static public ScopedSelection forScope(Scope scope, QName name) {
		return forScope(scope, name, null);
	}
	
	static public ScopedSelection forScope(Scope scope, QName name, Tag tag) {
		Class<? extends ScopedSelection> selectionClass = scope.getSelectionClass();
		try {
			ScopedSelection selection = selectionClass.newInstance();
			selection.configureFor(scope, name, tag);
			return selection;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public ScopedSelection() {
		//only to be used by JAXB
	}
	
	protected void configureFor(Scope scope, QName name, Tag tag) {
		this.name = name;
		this.scope = scope;
		this.tag = tag;
	}
		
	protected Scope scope;
	
	protected QName name;
	
	protected Tag tag;
	
	
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
	

	
	//to avoid recomputation for type/method scopes, could go into QName but there it would consume bytes
	//should move to type/method scopes when refactored into a scope hierarchy 
	private QName methodPackageName;
	private QName typePackageName;
	
	protected QName getMethodPackageName() {
		if(methodPackageName == null) {
			methodPackageName = getTypePackageName().getButLast();
		}
		return methodPackageName;
	}
	
	protected QName getTypePackageName() {
		if(typePackageName == null) {
			typePackageName = name.getButLast();
		}
		return typePackageName;
	}
	
	
	@Override
	public boolean matches(PackageTree packageTree) {
		return false;
	}
	
	@Override
	public boolean matches(PackageLayer packageLayer) {
		return false;
	}
	
	@Override
	public boolean matches(Member member) {
		return false;
	}

	
	@Override
	public boolean matches(Ref ref) {
		return false;
	}

	
	@Override
	public boolean mayContainMatches(PackageLayer packageLayer) {
		return false;
	}

	@Override
	public boolean mayContainMatches(Member member) {
		return false;
	}
	
	@Override
	public boolean mayContainMatches(PackageTree packageTree) {
		return true;
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
