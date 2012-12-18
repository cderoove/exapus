package exapus.model.view;

public class APICentricView extends View {

	public APICentricView(String name) {
		super(name, Perspective.API_CENTRIC);
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
