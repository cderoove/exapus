package exapus.model.forest;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;

import exapus.model.visitors.IForestVisitor;

public class ExapusModel {

	private InboundFactForest apis;
	
	private OutboundFactForest projects;

	private void resetForests() {
		projects = new OutboundFactForest(this);
		apis = new InboundFactForest(this);
	}

	public ExapusModel() {
		resetForests();
	}

	public void setAPICentricForest(InboundFactForest forest) {
		apis = forest;
	}
	
	public void setProjectCentricForest(OutboundFactForest forest) {
		projects = forest;
	}
	
	public InboundFactForest getAPICentricForest() {
		return apis;
	}

	public OutboundFactForest getProjectCentricForest() {
		return projects;
	}

	public IStatus processWorkspace(IProgressMonitor m) throws CoreException {
		IProject[] iprojects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		return processProjects(iprojects, m);
	}

	public IStatus processProjects(IProject[] ps, IProgressMonitor m) throws CoreException {
        long startTime = System.currentTimeMillis();

		m.beginTask("Processing projects", ps.length);
		for (IProject p : ps) {
			m.subTask("Processing project " + p.getName());
			if (m.isCanceled()) {
				m.done();
				resetForests();
				return Status.CANCEL_STATUS;
			}
			processProject(p, null); // new SubProgressMonitor(m, 1));
			m.worked(1);
		}
		m.done();

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.err.printf("Loading projects: %d s\n", elapsedTime / 1000);

        return Status.OK_STATUS;
	}
	
	
	public void processProject(IProject p, IProgressMonitor m) throws CoreException {
		if (p.isOpen()) {
			if(!p.getName().startsWith("__PPA_PROJECT")) {
				projects.addProject(p, m);
			}
		}
	}
	
	/*
	public void acceptVisitor(IForestVisitor v) {
		if(v.visitModel(this)) {
			getProjectCentricForest().acceptVisitor(v);
			getAPICentricForest().acceptVisitor(v);
		}
		
	}
	*/

}
