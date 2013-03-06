package exapus.model.view;

public enum Perspective {
	
	API_CENTRIC("API"),
	PROJECT_CENTRIC("Project");
	
	private Perspective(String shortLabel) {
		this.shortLabel = shortLabel;
	}
	
	public static Perspective[] supportedPerspectives() {
		return Perspective.class.getEnumConstants();
	}
	
	private String shortLabel;
	
	public String getShortLabel() {
		return shortLabel;
	}
	
	public Perspective getDual() {
		return (this == API_CENTRIC ? PROJECT_CENTRIC : API_CENTRIC);
	}	
		

}
