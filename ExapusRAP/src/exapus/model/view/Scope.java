package exapus.model.view;

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

	

	
}
