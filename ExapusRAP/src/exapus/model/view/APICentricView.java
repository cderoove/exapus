package exapus.model.view;

public class APICentricView extends View {

	public APICentricView(String name, Selection apis, Selection projects) {
		super(name, Perspective.API_CENTRIC, apis, projects);
	}

	@Override
	public boolean isAPICentric() {
		return true;
	}

	@Override
	public boolean isProjectCentric() {
		return false;
	}
	

}
