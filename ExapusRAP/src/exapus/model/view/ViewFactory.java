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
		Selection universal = UniversalSelection.getCurrent();
		completeAPIView = new APICentricView("All APIs");
		completeAPIView.addAPISelection(universal);
		completeAPIView.addProjectSelection(universal);
		completeProjectView = new ProjectCentricView("All Projects");
		completeProjectView.addAPISelection(universal);
		completeProjectView.addProjectSelection(universal);
	}
	
	public View completeAPIView() {
		return completeAPIView;
	}
	
	public View completeProjectView() {
		return completeProjectView;
	}

}
