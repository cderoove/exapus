package exapus.model.view;

import exapus.model.forest.ForestElement;
import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.Ref;

public enum Scope {

	TAG_SCOPE("Selects elements carrying a tag with the given name.", TagScopedSelection.class),
	ROOT_SCOPE("Selects elements within and below the given root name.", RootScopedSelection.class),
	PREFIX_SCOPE("Selects elements within and below the given name.", PrefixScopedSelection.class),
	PACKAGE_SCOPE("Selects elements within (but not below) the given package name.", PackageScopedSelection.class),
	TYPE_SCOPE("Selects elements within and below the given type name.", TypeScopedSelection.class),
	METHOD_SCOPE("Selects elements within and below the given method name.", MethodScopedSelection.class);
		
	Scope(String description, Class<? extends ScopedSelection> selectionClass) {
		this.selectionClass = selectionClass;
		this.description = description;
	}
	
	
	public static Scope[] supportedSelectionScopes() {
		return Scope.class.getEnumConstants();
	}
	
	private String description;
	
	private Class<? extends ScopedSelection> selectionClass;
	
	public String getDescription() {
		return description;
	}
	
	public Class<? extends ScopedSelection> getSelectionClass() {
		return selectionClass;
	}
	
	public static Scope forTagging(PackageTree tree) {
		return ROOT_SCOPE;
	}

	public static Scope forTagging(PackageLayer layer) {
		return PACKAGE_SCOPE;
	}
	
	public static Scope forTagging(Member member) {
		if(member.getElement().isMethod())
			return METHOD_SCOPE;
		return TYPE_SCOPE; //includes field members
	}
	
	public static Scope forTagging(Ref ref) {
		Member parentMember =  ref.getParentMember();
		return forTagging(parentMember);
	}
	
	public static Scope forTagging(ForestElement e) {
		if(e instanceof Ref)
			return forTagging((Ref) e);
		if(e instanceof Member)
			return forTagging((Member) e);
		if(e instanceof PackageLayer)
			return forTagging((PackageLayer) e);
		if(e instanceof PackageTree)
			return forTagging((PackageTree) e);
		return null;		
	}
	
	
}
