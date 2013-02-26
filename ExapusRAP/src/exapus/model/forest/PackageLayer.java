package exapus.model.forest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.google.common.collect.Iterables;

import exapus.model.visitors.IForestVisitor;

public class PackageLayer extends MemberContainer implements ILayerContainer {

	public PackageLayer(UqName n) {
		super(n);
		layers = new ArrayList<PackageLayer>();
	}	

	public String toString() {
		return "PL[" + getName().toString() + " | M(" + members.size() + ")" + ",PL(" + layers.size() + ")]";
	}

	public Iterable<PackageLayer> getPackageLayers() {
		return layers;
	}

	public Iterable<PackageLayer> getAllPackageLayers() {
		Iterable<PackageLayer> layers = new ArrayList<PackageLayer>();
		for(PackageLayer l : getPackageLayers()) {
			layers = Iterables.concat(layers, l.getPackageLayers());
		}
		return layers;
	}



	public PackageLayer getOrAddLayer(Iterator<UqName> i, PackageTree project) {
		PackageLayer l = getOrAddLayer(i.next(), project);
		if (i.hasNext())
			return l.getOrAddLayer(i, project);
		else
			return l;
	}


	public PackageLayer getLayer(UqName name) {
		for (PackageLayer l : layers)
			if (l.getName().equals(name))
				return l;
		return null;
	}


	public PackageLayer getOrAddLayer(QName qname, PackageTree project) {
		return getOrAddLayer(qname.getComponents().iterator(), project);
	}

	public PackageLayer getOrAddLayer(UqName name, PackageTree project) {
		for (PackageLayer l : layers)
			if (l.getName().equals(name))
				return l;

		PackageLayer l = new PackageLayer(name);
		layers.add(l);
		if(project.getHiddenRootLayer().equals(this))
			l.setParent(project);
		else
			l.setParent(this);
		getParentFactForest().fireUpdate(l);
		return l;
	}

	public void addBodyDeclaration(BodyDeclaration bd, Stack<ASTNode> scope) {
		getOrAddMember(UqName.forNode(bd), Element.forNode(bd), scope.iterator());
	}

	public void addMethodDeclaration(MethodDeclaration md, Stack<ASTNode> scope, IMethodBinding mb) {
		getOrAddMember(UqName.forBinding(mb), Element.forNode(md), scope.iterator());
	}


	public void addAnonymousClassDeclaration(AnonymousClassDeclaration bd, Stack<ASTNode> scope) {
		getOrAddMember(UqName.forNode(bd), Element.forNode(bd), scope.iterator());
	}

	public void addOutboundReference(ASTNode n, ITypeBinding b, Stack<ASTNode> scope) {
		Member enclosingMember = getOrAddMember(scope.iterator());
		OutboundRef outbound = OutboundRef.fromNodeToBinding(n, b);
		enclosingMember.addAPIReference(outbound);
		getAPICentricForest().addInboundAPIReference(b, outbound);
	}

	public void addOutboundReference(ASTNode n, IVariableBinding fb, ITypeBinding tb, Stack<ASTNode> scope) {
		Member enclosingMember = getOrAddMember(scope.iterator());
		OutboundRef reference = OutboundRef.fromNodeToBinding(n, fb, tb);
		enclosingMember.addAPIReference(reference);
		getAPICentricForest().addInboundAPIReference(tb, fb, reference);

	}

	public void addOutboundReference(ASTNode n, IMethodBinding mb, ITypeBinding tb, Stack<ASTNode> scope) {
		Member enclosingMember = getOrAddMember(scope.iterator());
		OutboundRef reference = OutboundRef.fromNodeToBinding(n, mb, tb);
		enclosingMember.addAPIReference(reference);
		getAPICentricForest().addInboundAPIReference(tb, mb, reference);

	}

	public void processCompilationUnits(IJavaProject p, ICompilationUnit[] icus) {
		CompilationUnit[] cus = Parser.parse(p, icus, null);
		for (CompilationUnit cu : cus)
			cu.accept(new LayerPopulatingVisitor(this));
	}


	private List<PackageLayer> layers;

	@Override
	public Member getParentMember() {
		return null;
	}

	@Override
	public ICompilationUnit getCorrespondingICompilationUnit() {
		return null;
	}

	@Override
	public Iterable<Member> getAllMembers() {
		Iterable<Member> members = super.getAllMembers();
		for(PackageLayer l : getAllPackageLayers()) {
			members = Iterables.concat(members, l.getAllMembers());
		}
		return members;
	}


	public Iterable<Ref> getAllReferences() {
		Iterable<Ref> references = new ArrayList<Ref>();
		for(Member m : getAllMembers()) {
			references = Iterables.concat(references, m.getAllReferences());
		}
		return references;
	}

	public void addInboundReference(ITypeBinding typeBinding, OutboundRef outbound) {
		Member enclosingMember = getOrAddMember(typeBinding);
		InboundRef inbound = outbound.toInboundRef();
		enclosingMember.addAPIReference(inbound);
	}

	public void addInboundReference(IMethodBinding methodBinding, OutboundRef outbound) {
		Member enclosingMember = getOrAddMember(methodBinding);
		InboundRef inbound = outbound.toInboundRef();
		enclosingMember.addAPIReference(inbound);

	}

	public void addInboundReference(IVariableBinding variableBinding, OutboundRef outbound) {
		Member enclosingMember = getOrAddMember(variableBinding);
		InboundRef inbound = outbound.toInboundRef();
		enclosingMember.addAPIReference(inbound);
	}

	public void acceptVisitor(IForestVisitor v) {
		if(v.visitPackageLayer(this)) {
			for(PackageLayer l : getPackageLayers())
				l.acceptVisitor(v);
			for(Member m : getMembers())
				m.acceptVisitor(v);
		}
	}

	public void addLayer(PackageLayer l) {
		layers.add(l);
		l.setParent(this);
	}

	public Ref copyReference(Iterator<ForestElement> ancestors, Ref original) {
		ForestElement ancestor = ancestors.next();
		if(ancestor instanceof PackageLayer) {
			PackageLayer destinationLayer = getOrAddLayer(ancestor.getName(), this.getParentPackageTree());
			return destinationLayer.copyReference(ancestors, original);
		}
		if(ancestor instanceof Member) {
			Member originalMember = (Member) ancestor;
			Member destinationMember = getOrAddMember(originalMember.getName(), originalMember.getElement());
			return destinationMember.copyReference(ancestors, original);
		}
		return null;
	}

	@Override
	ForestElement getCorrespondingForestElement(Iterator<ForestElement> ancestors, ForestElement element) {
		ForestElement ancestor = ancestors.next();
		if(ancestor instanceof PackageLayer) {
			PackageLayer correspondingLayer = getLayer(ancestor.getName());
			if(correspondingLayer == null) 
				return null;
			if(ancestors.hasNext())
				return correspondingLayer.getCorrespondingForestElement(ancestors, element);
			return correspondingLayer.getCorrespondingForestElement(element);
		}
		if(ancestor instanceof Member) {
			Member originalMember = (Member) ancestor;
			Member correspondingMember = getMember(originalMember.getName(), originalMember.getElement());
			if(correspondingMember == null)
				return null;
			if(ancestors.hasNext())
				return correspondingMember.getCorrespondingForestElement(ancestors, element);
			return correspondingMember.getCorrespondingForestElement(element);
		}
		return null;
	}

	@Override
	ForestElement getCorrespondingForestElement(ForestElement element) {
		if(element instanceof PackageLayer) 
			return getLayer(element.getName());
		return super.getCorrespondingForestElement(element);
	}


}
