package exapus.model.view;

public class ProjectCentricView extends View {
	
	public ProjectCentricView(String name) {
		super(name, Perspective.PROJECT_CENTRIC);
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
