package exapus.model.view;

public class View {

	public View(Perspective p, Selection apis, Selection projects) {
		perspective = p;
		apiselection = apis;
		projectselection = projects;
	}
	
	private Perspective perspective;
	
	private Selection apiselection;
	
	private Selection projectselection;

	public Perspective getPerspective() {
		return perspective;
	}

	public void setPerspective(Perspective perspective) {
		this.perspective = perspective;
	}

	public Selection getAPISelection() {
		return apiselection;
	}

	public void setAPISelection(Selection selection) {
		this.apiselection = selection;
	}

	public Selection getProjectSelection() {
		return projectselection;
	}

	public void setProjectSelection(Selection selection) {
		this.projectselection = selection;
	}
	
	
	
}
