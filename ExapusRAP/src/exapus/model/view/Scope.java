package exapus.model.view;

public enum Scope {

	ROOT_SCOPE, PREFIX_SCOPE, PACKAGE_SCOPE, TYPE_SCOPE, METHOD_SCOPE;
	
	private static Scope[] supportedSelectionScopes = {ROOT_SCOPE, PREFIX_SCOPE, PACKAGE_SCOPE, TYPE_SCOPE, METHOD_SCOPE};
	public static Scope[] supportedSelectionScopes() {
		return supportedSelectionScopes;
	}

	
}
