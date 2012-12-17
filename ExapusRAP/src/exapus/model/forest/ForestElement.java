package exapus.model.forest;

import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.google.common.collect.Iterables;

import exapus.gui.editors.forest.graph.INode;

public abstract class ForestElement implements INode {

	private ForestElement parent;

	private UqName id;

		
	public ForestElement(UqName id) {
		this.id = id;
	}
	
	public void setName(UqName id) {
		this.id = id;
	}

	public UqName getName() {
		return id;
	}

	public QName getQName() {
		LinkedList<String> components = new LinkedList<String>();
		ForestElement parent = this;
		while (parent != null && !(parent instanceof PackageTree)) {
			components.addFirst(parent.getName().toString());
			parent = parent.getParent();
		}
		QName name = new QName(components);
		return name;
	}

	public ForestElement getParent() {
		return parent;
	}

	public void setParent(ForestElement parent) {
		this.parent = parent;
	}

	public PackageLayer getParentPackageLayer() {
		ForestElement parent = getParent();
		while (parent != null && !(parent instanceof PackageLayer))
			parent = parent.getParent();
		return (PackageLayer) parent;
	}

	public Member getParentMember() {
		ForestElement parent = getParent();
		while (parent != null && !(parent instanceof Member))
			parent = parent.getParent();
		return (Member) parent;
	}
	
	public Member getParentTypeDeclaringMember() {
		Member parentMember = getParentMember();
		if(parentMember == null)
			return null;
		if(parentMember.getElement().declaresType()) {
			return parentMember;
		} else {
			return parentMember.getParentMember();
		}
	}

	public PackageTree getParentPackageTree() {
		ForestElement parent = getParent();
		while (parent != null && !(parent instanceof PackageTree))
			parent = parent.getParent();
		return (PackageTree) parent;
	}

	public FactForest getParentFactForest() {
		return getParentPackageTree().getParentFactForest();
	}
	
	
	
	public ExapusModel getExapusModel() {
		return getParentFactForest().getModel();
	}
	
	public InboundFactForest getAPICentricForest() {
		return getExapusModel().getAPICentricForest();
	}
	

	public InboundFactForest getProjectCentricForest() {
		return getExapusModel().getAPICentricForest();
	}
	
	public FactForest getDualFactForest() {
		return getParentFactForest().getDualFactForest();
	}

	public IProject getCorrespondingIProject() {
		PackageTree packageTree = getParentPackageTree();
		IProject project = packageTree.getCorrespondingIProject();
		return project;
	}

	public IJavaProject getCorrespondingJavaProject() {
		return JavaCore.create(getCorrespondingIProject());
	}

	public abstract ICompilationUnit getCorrespondingICompilationUnit();

	public String getSourceString() {
		ICompilationUnit icu = getCorrespondingICompilationUnit();
		if(icu == null)
			return null;
		try {
			return icu.getSource();
		} catch (JavaModelException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int getSourceCharacterIndexOffset() {
		return 0;
	}
	
	public int getSourceLineNumberOffset() {
		return 0;
	}

	
}
