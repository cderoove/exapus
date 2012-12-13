package exapus.model.viewdefinition;

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
		completeAPIView = new APICentricView(UniversalSelection.getCurrent(),UniversalSelection.getCurrent());
		completeProjectView = new ProjectCentricView(UniversalSelection.getCurrent(),UniversalSelection.getCurrent());
	}
	
	public View completeAPIView() {
		return completeAPIView;
	}
	
	public View completeProjectView() {
		return completeProjectView;
	}

}
