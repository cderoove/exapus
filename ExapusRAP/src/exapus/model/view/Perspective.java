package exapus.model.view;

public enum Perspective {
	
	API_CENTRIC, PROJECT_CENTRIC;
	
	public static Perspective[] supportedPerspectives() {
		return Perspective.class.getEnumConstants();
	}

}
