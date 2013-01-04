package exapus.model.view;

public enum Perspective {
	
	API_CENTRIC, PROJECT_CENTRIC;
	
	private static 	Perspective[] supportedPerspectives = {Perspective.API_CENTRIC, Perspective.PROJECT_CENTRIC};
	public static Perspective[] supportedPerspectives() {
		return supportedPerspectives;
	}

}
