package exapus.model.view;

public class ProjectCentricView extends View {
	
	public ProjectCentricView(String name, Selection apis, Selection projects) {
		super(name, Perspective.PROJECT_CENTRIC, apis, projects);
	}

	@Override
	public boolean isAPICentric() {
		return false;
	}

	@Override
	public boolean isProjectCentric() {
		return true;
	}
	
}
