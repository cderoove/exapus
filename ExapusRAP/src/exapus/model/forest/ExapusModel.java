package exapus.model.forest;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;

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
		
		return Status.OK_STATUS;
	}
	
	
	public void processProject(IProject p, IProgressMonitor m) throws CoreException {
		if (p.isOpen() && p.isNatureEnabled(JavaCore.NATURE_ID))
			projects.addProject(JavaCore.create(p), m);
	}

}
