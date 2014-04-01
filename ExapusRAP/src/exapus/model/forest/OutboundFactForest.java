package exapus.model.forest;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;

//import ca.mcgill.cs.swevo.ppa.PPAOptions;
//import ca.mcgill.cs.swevo.ppa.util.PPACoreUtil;
import exapus.model.visitors.IForestVisitor;

public class OutboundFactForest extends FactForest {

	public OutboundFactForest(ExapusModel m) {
		super(m, Direction.OUTBOUND);
	}

	public void addJavaProject(PackageTree tree, IJavaProject p, IProgressMonitor m) throws JavaModelException {
        try {
            IPackageFragment[] packageFragments = p.getPackageFragments();
            for (IPackageFragment f : packageFragments) {
                // m.subTask("Processing project package: " + f.getElementName());
                tree.processSourcePackageFragment(f);
                // m.worked(1);
            }
            // m.done();

        } catch (Exception ex){
            if (ex instanceof JavaModelException) throw (JavaModelException) ex;
            System.err.printf("Project %s had a problem loading\n", p.getElementName());
            ex.printStackTrace();
        }
	}
	
    
    private static boolean isHidden(IPath path) {
        String segment = path.lastSegment();
        if(segment == null){
                return false;
        }
        return segment.startsWith(".");
    }

    
	public void addPartialJavaProject(PackageTree tree, IProject p, IProgressMonitor m) throws JavaModelException {
		LinkedList<IResource> worklist = new LinkedList<IResource>();
		LinkedList<File> partialJavaFiles = new LinkedList<File>();
		try {
			Collections.addAll(worklist, p.members());
			while(!worklist.isEmpty()) {
				IResource resource = worklist.removeFirst();
	        	IPath path = resource.getFullPath();
		        if (resource instanceof IFolder) {
	                IFolder folder = (IFolder) resource;
	                if(!isHidden(path)){
	                	Collections.addAll(worklist, folder.members());
	                }
		        } else if (resource instanceof IFile) {
		        	if("java".equals(path.getFileExtension())){
		        		String osPath = resource.getRawLocation().toOSString();
                        partialJavaFiles.addFirst(new File(osPath));
		            }	
		        }
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	
		/*
		//XXX: fiddle with request name to bypass threading problem
        List<CompilationUnit> cus = PPACoreUtil.getCUs(partialJavaFiles, new PPAOptions(true, true, true, true, 512));
        		
        		//new PPAOptions()); //, "ExapusWorker");
        
        		 //new PPAOptions(true,true,true,true,-1));
        Set<String> sourcePackageNames = getSourcePackageNames(cus);       
        
        for(CompilationUnit cu : cus) {
        	//list contains null 
        	if(cu != null)
        		tree.processPartialCompilationUnit(cu, sourcePackageNames);       
        	
        	//PPACoreUtil.cleanUp(cu);
        }

    	//PPACoreUtil.cleanUpAll();
     */       
	}
        
	

	public Set<String> getSourcePackageNames(List<CompilationUnit> cus) {
		Set<String> sourcePackageNames = new HashSet<String>();
		for(CompilationUnit cu : cus) {
			if(cu != null) {
				PackageDeclaration pDec = cu.getPackage();
				if (pDec != null) {
					sourcePackageNames.add(pDec.getName().getFullyQualifiedName());
				}		
			}			
		}

		return sourcePackageNames;
	}
	
	public void addProject(IProject p, IProgressMonitor m) throws JavaModelException {
        long startTime = System.currentTimeMillis();

        String name = p.getName();
        // m.beginTask("Processing project: " + name, packageFragments.length);
        System.err.printf("Processing project %s \n", name);
        
        
        UqName projectName = new UqName(name);
        PackageTree tree = new PackageTree(projectName);
        addPackageTree(tree);
        
        try {
			if(p.isNatureEnabled(JavaCore.NATURE_ID)) {
				addJavaProject(tree, JavaCore.create(p), m);
			} else {
		        System.err.printf("Ignoring project %s because it has no Java nature \n", name);
				//addPartialJavaProject(tree, p, m);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
        
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.printf("Added project (%d) %s to project-centric forest (%d s)\n", trees.keySet().size(), name, elapsedTime / 1000);


		
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
