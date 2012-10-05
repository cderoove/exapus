package exapus.model;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public enum Element {
	INSTANCE_METHOD, STATIC_METHOD, CONSTRUCTOR, INSTANCE_FIELD, STATIC_FIELD, INTERFACE, CLASS,
	// added
	ENUM, ENUM_CONSTANT, ANONYMOUS_CLASS, ANNOTATION_TYPEDECLARATION;

	public boolean declaresType() {
		return this == INTERFACE || this == CLASS || this == ENUM || this == ENUM_CONSTANT || this == ANONYMOUS_CLASS || this == ANNOTATION_TYPEDECLARATION;
	}
	
	public boolean isField() {
		return this == INSTANCE_FIELD || this == STATIC_FIELD;
	}
	
	public boolean isMethod() {
		return this == INSTANCE_METHOD || this ==  STATIC_METHOD || this == CONSTRUCTOR;
	}

	static Element forNode(ASTNode bd) {

		if (bd instanceof MethodDeclaration) {
			MethodDeclaration m = (MethodDeclaration) bd;
			if (Modifier.isStatic(m.getModifiers()))
				return STATIC_METHOD;
			else {
				if (m.isConstructor())
					return CONSTRUCTOR;
				else
					return INSTANCE_METHOD;
			}
		}

		if (bd instanceof FieldDeclaration) {
			FieldDeclaration f = (FieldDeclaration) bd;
			if (Modifier.isStatic(f.getModifiers()))
				return STATIC_FIELD;
			else
				return INSTANCE_FIELD;
		}

		if (bd instanceof TypeDeclaration) {
			TypeDeclaration t = (TypeDeclaration) bd;
			if (t.isInterface())
				return INTERFACE;
			else
				return CLASS;
		}

		if (bd instanceof EnumDeclaration) {
			EnumDeclaration e = (EnumDeclaration) bd;
			return ENUM;
		}

		if (bd instanceof EnumConstantDeclaration) {
			EnumConstantDeclaration e = (EnumConstantDeclaration) bd;
			return ENUM_CONSTANT;
		}

		if (bd instanceof AnonymousClassDeclaration) {
			return ANONYMOUS_CLASS;
		}

		if (bd instanceof AnnotationTypeDeclaration) {
			return ANNOTATION_TYPEDECLARATION;
		}

		throw new Error("Don't know how to create forest Element for given scope-definining JDT node");

	}

	public static Element forBinding(ITypeBinding apiBinding) {
		if (apiBinding.isAnonymous())
			return ANONYMOUS_CLASS;
		if (apiBinding.isClass())
			return CLASS;
		if (apiBinding.isInterface())
			return INTERFACE;
		if (apiBinding.isEnum())
			return ENUM;
		if (apiBinding.isAnnotation())
			return ANNOTATION_TYPEDECLARATION;
		throw new Error("Don't know how to create forest Element for given API binding.");
	}
	
	public static Element forBinding(IMethodBinding apiBinding) {
		if (apiBinding.isConstructor())
			return CONSTRUCTOR;
		if (Modifier.isStatic(apiBinding.getModifiers()))
			return STATIC_METHOD;
		else
			return INSTANCE_METHOD;
	}

	
	public static Element forBinding(IVariableBinding fb) {
		if(fb.isEnumConstant())
			return ENUM_CONSTANT;
		if (Modifier.isStatic(fb.getModifiers()))
			return STATIC_FIELD;
		else
			return INSTANCE_FIELD;
	}

}
