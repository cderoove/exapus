package exapus.model.view;

public class ViewFactory {
	
	private static ViewFactory current;
	
	static {
		current = new ViewFactory();
	}

	public static ViewFactory getCurrent() {
		return current;
	}
	
	
	private View completePackageView;
	private View completeProjectView;
	
	
	private ViewFactory() {
		Selection universal = UniversalSelection.getCurrent();
		completePackageView = new APICentricView("All Packages");
		completePackageView.addAPISelection(universal);
		completePackageView.addProjectSelection(universal);
		completePackageView.setRenderable(false);
		
		completeProjectView = new ProjectCentricView("All Projects");
		completeProjectView.addAPISelection(universal);
		completeProjectView.addProjectSelection(universal);
		completeProjectView.setRenderable(false);

	}
	
	public View completePackageView() {
		return completePackageView;
	}
	
	public View completeProjectView() {
		return completeProjectView;
	}

}
