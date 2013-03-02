package exapus.model.forest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;

import com.google.common.collect.Iterables;

import exapus.model.visitors.IForestVisitor;

public class PackageTree extends ForestElement  implements ILayerContainer  {

	private FactForest forest;

	private PackageLayer root;

	public PackageTree(UqName name) {
		super(name);
		root = new PackageLayer(new UqName("<rootLayer>"));
		root.setParent(this); // TODO: this might cause the root layer to show
		// up in views
	}
	
	@Override
	public QName getQName() {
		return new QName(getName());
	}

	public PackageLayer getHiddenRootLayer() {
		return root;
	}

	public Iterable<PackageLayer> getPackageLayers() {
		return root.getPackageLayers();
	}
	
	public Iterable<PackageLayer> getAllPackageLayers() {
		return root.getAllPackageLayers();
	}


	public void processSourcePackageFragment(IPackageFragment f) throws JavaModelException {
		if (f.getKind() != IPackageFragmentRoot.K_SOURCE)
			return;
		if (!f.containsJavaResources())
			return;
		PackageLayer layer = getOrAddLayerForPackageFragment(f);
		ICompilationUnit[] compilationUnits = f.getCompilationUnits();
		layer.processCompilationUnits(f.getJavaProject(), compilationUnits);
	}

	public FactForest getParentFactForest() {
		return forest;
	}

	public void setFactForest(FactForest forest) {
		this.forest = forest;
	}

	public String toString() {
		return "T[" + getName() + "]";
	}

	public IProject getCorrespondingIProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(getName().toString());
	}

	@Override
	public Member getParentMember() {
		return null;
	}

	@Override
	public ICompilationUnit getCorrespondingICompilationUnit() {
		return null;
	}


	public Iterable<Member> getAllMembers() {
		return root.getAllMembers();
	}

	public Iterable<Ref> getAllReferences() {
		return root.getAllReferences();
	}

	
	public void addLayer(PackageLayer l) {
		root.addLayer(l);
		l.setParent(this);
	}
	
	public PackageLayer getOrAddLayer(UqName name) {
		return root.getOrAddLayer(name, this);
	}
	
	public PackageLayer getLayer(UqName name) {
		return root.getLayer(name);
	}

	
	
	public PackageLayer getOrAddLayerForPackageFragment(IPackageFragment packageFragment) {
		QName qname = new QName(packageFragment);
		PackageLayer layer = root.getOrAddLayer(qname, this);
		getParentFactForest().fireUpdate(layer);
		return layer;
	}


	private PackageLayer getOrAddLayerCorrespondingToTypeBinding(ITypeBinding t) {
		IType type = (IType) t.getJavaElement();
		IPackageFragment packageFragment = type.getPackageFragment();
		PackageLayer layer = getOrAddLayerForPackageFragment(packageFragment);
		return layer;
	}

	public void addInboundReference(ITypeBinding b, OutboundRef outbound) {
		PackageLayer layer = getOrAddLayerCorrespondingToTypeBinding(b);
		layer.addInboundReference(b, outbound);
	}

	public void addInboundReference(ITypeBinding t, IMethodBinding b, OutboundRef outbound) {
		PackageLayer layer = getOrAddLayerCorrespondingToTypeBinding(t);
		layer.addInboundReference(b, outbound);
	}

	public void addInboundReference(ITypeBinding t, IVariableBinding b, OutboundRef outbound) {
		PackageLayer layer = getOrAddLayerCorrespondingToTypeBinding(t);
		layer.addInboundReference(b, outbound);
	}

	public void acceptVisitor(IForestVisitor v) {
		if(v.visitPackageTree(this))
			for(PackageLayer l : getPackageLayers())
				l.acceptVisitor(v);
	}

	public Ref copyReference(Ref original) {
		ForestElement parent = original.getParent();
		LinkedList<ForestElement> parents = new LinkedList<ForestElement>();
		while (parent != null && !(parent instanceof PackageTree)) {
			parents.addFirst(parent);
			parent = parent.getParent();
		}
		Iterator<ForestElement> ancestor = parents.iterator();
		return copyReference(ancestor, original);
	}

	public Ref copyReference(Iterator<ForestElement> ancestors, Ref original) {
		ForestElement originalAncestor = ancestors.next();
		PackageLayer  originalLayer = (PackageLayer) originalAncestor;
		if(originalLayer == null)
			return null;
		PackageLayer destinationLayer = getOrAddLayer(originalLayer.getName());
		return destinationLayer.copyReference(ancestors, original);
	}

	ForestElement getCorrespondingForestElement(boolean copyWhenMissing, Iterator<ForestElement> ancestors, ForestElement element) {
		ForestElement originalLayer = ancestors.next();
		PackageLayer correspondingLayer = getLayer(originalLayer.getName());
		if(correspondingLayer == null)
			return null;
		if(ancestors.hasNext())
			return correspondingLayer.getCorrespondingForestElement(copyWhenMissing, ancestors, element);
		return correspondingLayer.getCorrespondingForestElement(copyWhenMissing, element);
	}

	ForestElement getCorrespondingForestElement(boolean copyWhenMissing, ForestElement element) {
		if(element instanceof PackageLayer) {
			UqName name = element.getName();
			if(copyWhenMissing)
				return getOrAddLayer(name);
			else
				return getLayer(name);
		}
		return null;
	}
	
	
	

}
