package exapus.model.view;

public class ViewFactory {
	
	private static ViewFactory current;
	
	static {
		current = new ViewFactory();
	}

	public static ViewFactory getCurrent() {
		return current;
	}
	
	
	private View completeAPIView;
	private View completeProjectView;
	
	
	private ViewFactory() {
		completeAPIView = new APICentricView("All APIs", UniversalSelection.getCurrent(),UniversalSelection.getCurrent());
		completeProjectView = new ProjectCentricView("All Projects", UniversalSelection.getCurrent(),UniversalSelection.getCurrent());
	}
	
	public View completeAPIView() {
		return completeAPIView;
	}
	
	public View completeProjectView() {
		return completeProjectView;
	}

}
