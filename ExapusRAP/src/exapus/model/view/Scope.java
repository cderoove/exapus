package exapus.model.view;

public enum Scope {

	ROOT_SCOPE("Selects elements within and below the given root name."),
	PREFIX_SCOPE("Selects elements within and below the given name."),
	PACKAGE_SCOPE("Selects elements within (but not below) the given package name."),
	TYPE_SCOPE("Selects elements within and below the given type name."),
	METHOD_SCOPE("Selects elements within and below the given method name.");
		
	Scope(String description) {
		this.description = description;
	}
	
	
	public static Scope[] supportedSelectionScopes() {
		return Scope.class.getEnumConstants();
	}
	
	private String description;
	
	public String getDescription() {
		return description;
	}
	

	
}
