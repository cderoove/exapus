package exapus.model;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;

public class OutboundFactForest extends FactForest {

	public OutboundFactForest(ExapusModel m) {
		super(m, Direction.OUTBOUND);
	}

	public void addProject(IJavaProject p, IProgressMonitor m) throws JavaModelException {
		String name = p.getElementName();
		IPackageFragment[] packageFragments = p.getPackageFragments();
		// m.beginTask("Processing project: " + name, packageFragments.length);
		UqName projectName = new UqName(name);
		PackageTree tree = new PackageTree(projectName);
		addPackageTree(tree);
		for (IPackageFragment f : packageFragments) {
			// m.subTask("Processing project package: " + f.getElementName());
			tree.processSourcePackageFragment(f);
			// m.worked(1);
		}
		// m.done();

		// System.out.println("Added project to project-centric forest: " +
		// name);
	}
	
	public FactForest getDualFactForest() {
		return getModel().getAPICentricForest();
	}
}
