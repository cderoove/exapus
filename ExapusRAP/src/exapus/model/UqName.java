package exapus.model;

import java.util.List;

import org.eclipse.core.commands.ParameterType;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public class UqName {

	public static UqName EMPTY = new UqName("");

	/*
	public static UqName forMethod(IMethod m) {
		StringBuilder name = new StringBuilder();
		name.append(m.getElementName());
		name.append("(");

		String[] parameterTypes = m.getParameterTypes();
		String[] parameterTypeNames = new String[parameterTypes.length];
		for (int i=0; i<parameterTypes.length; ++i) 
			parameterTypeNames[i] = Signature.toString(parameterTypes[i]);

		Joiner joiner = Joiner.on(", ");
		String parameters = joiner.join(parameterTypeNames);

		assert(parameters.indexOf('.') < 0); //otherwise, QName cannot use . to separate component names!!

		name.append(parameters);
		name.append(")");
		return new UqName(name.toString());
	}
	*/

	public static UqName forBinding(IMethodBinding mb) {
		StringBuilder name = new StringBuilder();
		name.append(mb.getName());
		name.append("(");		
		
		ITypeBinding[] parameterTypes = mb.getParameterTypes();
		String[] parameterTypeNames = new String[parameterTypes.length];
		for (int i=0; i<parameterTypes.length; ++i) 	
			parameterTypeNames[i] = parameterTypes[i].getName();
		
		Joiner joiner = Joiner.on(", ");
		String parameters = joiner.join(parameterTypeNames);

		assert(parameters.indexOf('.') < 0); //otherwise, QName cannot use . to separate component names!!

		
		name.append(parameters);
		name.append(")");
		return new UqName(name.toString());
	}

	public static UqName forBinding(IVariableBinding variableBinding) {
		return new UqName(variableBinding.getName());
	}

	public static UqName forBinding(ITypeBinding t) {
		QName name = QName.forBinding(t);
		List<UqName> components = name.getComponents();
		return components.get(components.size() - 1); //this should give us an (unqualified) name for anonymous classes
	}


	public static UqName forMethod(MethodDeclaration m) {
		IMethodBinding binding = m.resolveBinding();
		assert(binding != null);
		return forBinding(binding);
	}

	public static UqName forNode(ASTNode bd) {
		if (bd instanceof MethodDeclaration) {
			MethodDeclaration m = (MethodDeclaration) bd;
			return forMethod(m);
		}
		if (bd instanceof TypeDeclaration) {
			TypeDeclaration t = (TypeDeclaration) bd;
			return new UqName(t);
		}
		if (bd instanceof EnumDeclaration) {
			EnumDeclaration e = (EnumDeclaration) bd;
			return new UqName(e);
		}
		if (bd instanceof EnumConstantDeclaration) {
			EnumConstantDeclaration e = (EnumConstantDeclaration) bd;
			return new UqName(e.getName());
		}
		if (bd instanceof AnonymousClassDeclaration) {
			AnonymousClassDeclaration a = (AnonymousClassDeclaration) bd;
			ITypeBinding b = a.resolveBinding();
			if (b != null)
				return forBinding(b); 
			else
				return new UqName("<UNRESOLVEABLE_ANONYMOUSCLASS>"); // TODO: figure
			// out a way
			// to
			// disambiguate,
			// for
			// instance
			// using the
			// #anons
			// encountered
			// in a
			// scope
		}
		if (bd instanceof AnnotationTypeDeclaration) {
			AnnotationTypeDeclaration e = (AnnotationTypeDeclaration) bd;
			return new UqName(e);
		}

		throw new Error("Don't know how to create UqName for given scope-defining node.");
	}

	public UqName(String id) {
		identifier = id;
	}

	public UqName(IJavaProject p) {
		this(p.getElementName());
	}

	public UqName(TypeDeclaration td) {
		this(td.getName());
	}



	public String getMethodName() {
		String s = this.toString();
		int i = s.indexOf('(');
		if(i < 0)
			return null; //not a method name
		return s.substring(0, i);
	}

	public String[] getMethodParameterTypeSignatures() {
		String s = this.toString();
		int i = s.indexOf('(');
		if(i < 0)
			return null; //not a method name
		s = s.substring(i+1, s.length() - 1);
		if(s.length() == 0)
			return new String[0];
		Iterable<String> parameterTypeStrings = Splitter.on(',').split(s);
		Iterable<String> parameterTypeSignatures = 
				Iterables.transform(parameterTypeStrings, new Function<String,String> () {
					public String apply(String parameterTypeString) {
						return Signature.createTypeSignature(parameterTypeString.toCharArray(), false);
					}
				});
		return Iterables.toArray(parameterTypeSignatures, String.class);
	}


	public UqName(SimpleName n) {
		this(n.getIdentifier());
	}

	public UqName(EnumDeclaration e) {
		this(e.getName());
	}

	public UqName(AnnotationTypeDeclaration td) {
		this(td.getName());
	}

	private String identifier;

	public String toString() {
		return identifier;
	}

	// TODO: clean up equals/hashcode
	public boolean equals(Object o) {
		if (o instanceof UqName)
			return ((UqName) o).identifier.equals(identifier);
		return false;
	}

	public int hashCode() {
		return identifier.hashCode();
	}


}
