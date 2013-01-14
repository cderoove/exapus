package exapus.model.forest;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;

import exapus.model.visitors.IForestVisitor;

public class OutboundFactForest extends FactForest {

	public OutboundFactForest(ExapusModel m) {
		super(m, Direction.OUTBOUND);
	}

	public void addProject(IJavaProject p, IProgressMonitor m) throws JavaModelException {
        try {
            String name = p.getElementName();
            IPackageFragment[] packageFragments = p.getPackageFragments();
            // m.beginTask("Processing project: " + name, packageFragments.length);
            System.err.printf("Processing project %s (%d packages)\n", name, packageFragments.length);
            UqName projectName = new UqName(name);
            PackageTree tree = new PackageTree(projectName);
            addPackageTree(tree);
            for (IPackageFragment f : packageFragments) {
                // m.subTask("Processing project package: " + f.getElementName());
                tree.processSourcePackageFragment(f);
                // m.worked(1);
            }
            // m.done();

            System.out.printf("Added project %s to project-centric forest (%d)", name, trees.keySet().size());
        } catch (Exception ex){
            if (ex instanceof JavaModelException) throw (JavaModelException) ex;
            System.err.printf("Project %s had a problem loading\n", p.getElementName());
            ex.printStackTrace();
        }
	}

	public FactForest getDualFactForest() {
		return getModel().getAPICentricForest();
	}

	public void acceptVisitor(IForestVisitor v) {
		if(v.visitOutboundFactForest(this))
			for(PackageTree t : getPackageTrees())
				t.acceptVisitor(v);
	}


}
