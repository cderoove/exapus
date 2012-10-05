package exapus.model;

import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.WildcardType;

public class LayerPopulatingVisitor extends ASTVisitor {

	private PackageLayer layer;
	public Stack<ASTNode> scope; // cannot turn this into a
									// Stack<BodyDeclaration> because
									// AnonymousClassDeclaration isn't a
									// subclass

	public LayerPopulatingVisitor(PackageLayer l) {
		layer = l;
		scope = new Stack<ASTNode>();
	}

	public boolean visit(TypeDeclaration td) {
		layer.addBodyDeclaration(td, scope);
		scope.push(td);
		return true;
	}

	public void endVisit(TypeDeclaration td) {
		assert scope.peek() == td;
		scope.pop();
	}

	public boolean visit(AnnotationTypeDeclaration td) {
		layer.addBodyDeclaration(td, scope);
		scope.push(td);
		return true;
	}

	public void endVisit(AnnotationTypeDeclaration td) {
		assert scope.peek() == td;
		scope.pop();
	}

	public boolean visit(EnumConstantDeclaration bd) {
		layer.addBodyDeclaration(bd, scope);
		scope.push(bd);
		return true;
	}

	public void endVisit(EnumConstantDeclaration td) {
		assert scope.peek() == td;
		scope.pop();
	}

	public boolean visit(MethodDeclaration md) {
		IMethodBinding mb = md.resolveBinding();
		assert mb != null;
		layer.addMethodDeclaration(md, scope, mb);
		scope.push(md);
		for (Object o : md.thrownExceptions()) {
			Name n = (Name) o;
			considerThrownExceptionForAPITypeReference(n);
			// here instead of in visit(Name n) so that not every name has to be
			// asked for the location in its parent
			// to determine whether it resides in the list of thrown exceptions
		}
		return true;
	}

	public void endVisit(MethodDeclaration md) {
		assert scope.peek() == md;
		scope.pop();
	}

	public boolean visit(EnumDeclaration ec) {
		scope.push(ec);
		return true;
	}

	public void endVisit(EnumDeclaration ec) {
		assert scope.peek() == ec;
		scope.pop();
	}

	public boolean visit(AnonymousClassDeclaration td) {
		layer.addAnonymousClassDeclaration(td, scope);
		scope.push(td);
		return true;
	}

	public void endVisit(AnonymousClassDeclaration td) {
		assert scope.peek() == td;
		scope.pop();
	}

	public boolean visit(ImportDeclaration id) {
		return false;
	}

	public boolean visit(Annotation a) {
		IAnnotationBinding annotationBinding = a.resolveAnnotationBinding();
		ITypeBinding annotationTypeBinding = annotationBinding.getAnnotationType();
		if (isTypeBindingFromAPI(annotationTypeBinding))
			layer.addOutboundReference(a, annotationTypeBinding, scope);

		return true;

	}

	static boolean isTypeBindingFromAPI(ITypeBinding b) {
		if (b.isPrimitive())
			return false;
		if (b.isFromSource())
			return false;
		if (b.isArray()) {
			ITypeBinding elementType = b.getElementType();
			if (elementType.isFromSource())
				return false;
			if (elementType.isPrimitive())
				return false;
		}
		return true;
	}

	void considerForAPITypeReference(Type t) {
		ITypeBinding b = t.resolveBinding();
		if (b == null)
			return;
		b = b.getErasure();
		if (isTypeBindingFromAPI(b))
			layer.addOutboundReference(t, b, scope);
	}

	void considerThrownExceptionForAPITypeReference(Name n) {
		ITypeBinding b = n.resolveTypeBinding();
		if (b == null)
			return;
		b = b.getErasure();
		if (isTypeBindingFromAPI(b))
			layer.addOutboundReference(n, b, scope);
	}

	public boolean visit(ArrayType t) {
		// considerForAPIReference(t);
		return true;
	}

	public boolean visit(ParameterizedType t) {
		// considerForAPIReference(t);
		return true;
	}

	public boolean visit(PrimitiveType t) {
		return false;
	}

	public boolean visit(QualifiedType t) {
		considerForAPITypeReference(t);
		return false;
	}

	public boolean visit(SimpleType t) {
		considerForAPITypeReference(t);
		return false;
	}

	public boolean visit(UnionType t) {
		// considerForAPIReference(t);
		return true;
	}

	public boolean visit(WildcardType t) {
		// considerForAPIReference(t);
		return true;
	}

	public boolean visit(FieldAccess f) {
		considerForAPIFieldReference(f);
		return true;
	}

	public boolean visit(SuperFieldAccess f) {
		considerForAPIFieldReference(f);
		return true;
	}

	public boolean visit(MethodInvocation i) {
		considerForAPIMethodReference(i);
		return true;
	}

	public boolean visit(SuperMethodInvocation i) {
		considerForAPIMethodReference(i);
		return true;
	}

	public boolean visit(ClassInstanceCreation i) {
		// considerForAPIMethodReference(i); TODO: decide whether to consider
		// them an api constructor reference
		return true; // or merely consider them an api type reference
	}

	public boolean visit(ConstructorInvocation i) {
		considerForAPIMethodReference(i);
		return true;
	}

	public boolean visit(SuperConstructorInvocation i) {
		considerForAPIMethodReference(i);
		return true;
	}

	private void considerForAPIMethodReference(SuperConstructorInvocation i) {
		IMethodBinding mb = i.resolveConstructorBinding();
		if (mb == null)
			return;
		considerForAPIMethodReference(i, mb);
	}

	private void considerForAPIMethodReference(MethodInvocation i) {
		IMethodBinding mb = i.resolveMethodBinding();
		if (mb == null)
			return;
		considerForAPIMethodReference(i, mb);
	}

	private void considerForAPIMethodReference(SuperMethodInvocation i) {
		IMethodBinding mb = i.resolveMethodBinding();
		if (mb == null)
			return;
		considerForAPIMethodReference(i, mb);
	}

	private void considerForAPIMethodReference(ClassInstanceCreation i) {
		IMethodBinding mb = i.resolveConstructorBinding();
		if (mb == null)
			return;
		considerForAPIMethodReference(i, mb);
	}

	private void considerForAPIMethodReference(ConstructorInvocation i) {
		IMethodBinding mb = i.resolveConstructorBinding();
		if (mb == null)
			return;
		considerForAPIMethodReference(i, mb);
	}

	private void considerForAPIMethodReference(ASTNode n, IMethodBinding mb) {
		ITypeBinding tb = mb.getDeclaringClass();
		if (tb == null)
			return;
		tb = tb.getErasure();
		if (isTypeBindingFromAPI(tb))
			layer.addOutboundReference(n, mb, tb, scope);
	}

	private void considerForAPIFieldReference(FieldAccess f) {
		IVariableBinding fb = f.resolveFieldBinding();
		if (fb == null)
			return;
		considerForAPIFieldReference(f, fb);
	}

	private void considerForAPIFieldReference(ASTNode f, IVariableBinding fb) {
		ITypeBinding tb = fb.getDeclaringClass();
		if (tb == null)
			return;
		tb = tb.getErasure();
		if (isTypeBindingFromAPI(tb))
			layer.addOutboundReference(f, fb, tb, scope);
	}

	private void considerForAPIFieldReference(SuperFieldAccess f) {
		IVariableBinding fb = f.resolveFieldBinding();
		if (fb == null)
			return;
		considerForAPIFieldReference(f, fb);
	}

	void considerForAPIFieldReference(Name n) {
		IBinding b = n.resolveBinding();
		if (b == null)
			return;
		if (IBinding.VARIABLE != b.getKind())
			return;
		IVariableBinding ivb = (IVariableBinding) b;
		if (!ivb.isField())
			return;
		considerForAPIFieldReference(n, ivb);
		return;
	}

	public boolean visit(SimpleName n) {
		considerForAPIFieldReference(n);
		return false;
	}

	public boolean visit(QualifiedName n) {
		considerForAPIFieldReference(n);
		return false; // do not visit its simple names
	}

}
