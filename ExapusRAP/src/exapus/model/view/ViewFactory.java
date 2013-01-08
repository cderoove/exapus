package exapus.model.view;

import exapus.model.forest.QName;

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

	public View testAPICentricSelectionView() {
		View view = new APICentricView("API-centric selection test");
		view.addAPISelection(new ScopedSelection(new QName("java.lang.Integer"), Scope.TYPE_SCOPE));
		view.addAPISelection(new ScopedSelection(new QName("java.util.Iterator.hasNext()"), Scope.METHOD_SCOPE));
		view.addAPISelection(new ScopedSelection(new QName("javax"), Scope.PREFIX_SCOPE));
		view.addAPISelection(new ScopedSelection(new QName("org.apache.commons"), Scope.PREFIX_SCOPE));
		view.addAPISelection(new ScopedSelection(new QName("org.apache.tools.ant"), Scope.PACKAGE_SCOPE));
		view.addProjectSelection(UniversalSelection.getCurrent());
		view.setRenderable(false);

		return view;
	}
	
	public View testAPICentricSelectionView2() {
		View view = testAPICentricSelectionView();
		view.setName("API-centric selection test 2");
		view.removeProjectSelection(UniversalSelection.getCurrent());
		view.addProjectSelection(new ScopedSelection(new QName("org.sunflow"), Scope.PREFIX_SCOPE));
		view.addProjectSelection(new ScopedSelection(new QName("tomcat"), Scope.ROOT_SCOPE));		
		return view;
	}
	
	public View testProjectCentricSelectionView() {
		View view = new ProjectCentricView("Project-centric selection test");
		view.addProjectSelection(new ScopedSelection(new QName("sunflow"), Scope.ROOT_SCOPE));
		view.addAPISelection(new ScopedSelection(new QName("java.lang.String"), Scope.TYPE_SCOPE));
		view.setRenderable(false);
		return view;
	}
	
	
	
	

}
