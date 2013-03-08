package exapus.model.forest;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WildcardType;

public enum Pattern {

	INSTANCE_METHOD_CALL, SUPER_METHOD_CALL, STATIC_METHOD_CALL, CONSTRUCTOR_CALL, SUPER_CONSTRUCTOR_CALL,

	// TODO: might be better to keep it on FIELDACCESS / SUPERFIELDACCESS as
	// static-ness is also reflected by referenced ELEMENT
	INSTANCE_FIELD_ACCESS, // covered
	SUPER_FIELD_ACCESS, // covered
	STATIC_FIELD_ACCESS, // covered

	EXTENDS_CLASS, // covered
	IMPLEMENTS_INTERFACE, // covered
	EXTENDS_INTERFACE, // covered

	METHOD_PARAMETER, // covered
	METHOD_RESULT, // covered
	METHOD_THROWS, // covered

	FIELD_TYPE, // covered
	VARIABLE_TYPE, // covered
	ANNOTATIONMEMBER_TYPE, // covered, added

	CATCH_PARAMETER, // covered

	// next ones are covered, were not in paper, (BUT SHOULD MAYBE NOT BE ADDED
	// TO REF LIST OF TYPE MEMBERS) TODO
	INSTANCE_CREATION, // might be better to keep creation types, as to
						// differentiate them from constructor calls
	ARRAY_CREATION,

	CAST_TYPE, INSTANCEOF_TYPE, // not really a use

	PARAMETERIZED_TYPE_TYPE, // type of parameterized type
	PARAMETERIZED_TYPE_ARGUMENT, // one of the arguments of a parameterized type

	TYPE_BOUND, TYPE_UNION_MEMBER, // one of the types in a union type
	TYPE_ARRAY_COMPONENT,

	CLASS_TYPE,
	
	//only for facilitating selection against this particular kind
	ANY;

	static private Map<StructuralPropertyDescriptor, Pattern> type_mapping;

	static {
		type_mapping = new HashMap<StructuralPropertyDescriptor, Pattern>();
		type_mapping.put(VariableDeclarationExpression.TYPE_PROPERTY, VARIABLE_TYPE);
		type_mapping.put(VariableDeclarationStatement.TYPE_PROPERTY, VARIABLE_TYPE);
		type_mapping.put(MethodDeclaration.THROWN_EXCEPTIONS_PROPERTY, METHOD_THROWS);
		type_mapping.put(MethodDeclaration.RETURN_TYPE2_PROPERTY, METHOD_RESULT);
		type_mapping.put(FieldDeclaration.TYPE_PROPERTY, FIELD_TYPE);
		type_mapping.put(CastExpression.TYPE_PROPERTY, CAST_TYPE);
		type_mapping.put(ArrayType.COMPONENT_TYPE_PROPERTY, TYPE_ARRAY_COMPONENT);
		type_mapping.put(ClassInstanceCreation.TYPE_PROPERTY, INSTANCE_CREATION);
		type_mapping.put(InstanceofExpression.RIGHT_OPERAND_PROPERTY, INSTANCEOF_TYPE);
		type_mapping.put(ClassInstanceCreation.TYPE_ARGUMENTS_PROPERTY, PARAMETERIZED_TYPE_ARGUMENT);
		type_mapping.put(MethodInvocation.TYPE_ARGUMENTS_PROPERTY, PARAMETERIZED_TYPE_ARGUMENT);
		type_mapping.put(ParameterizedType.TYPE_ARGUMENTS_PROPERTY, PARAMETERIZED_TYPE_ARGUMENT);
		type_mapping.put(ParameterizedType.TYPE_PROPERTY, PARAMETERIZED_TYPE_TYPE);
		type_mapping.put(UnionType.TYPES_PROPERTY, TYPE_UNION_MEMBER);
		type_mapping.put(TypeParameter.TYPE_BOUNDS_PROPERTY, TYPE_BOUND);
		type_mapping.put(WildcardType.BOUND_PROPERTY, TYPE_BOUND);
		type_mapping.put(AnnotationTypeMemberDeclaration.TYPE_PROPERTY, ANNOTATIONMEMBER_TYPE);
		type_mapping.put(TypeLiteral.TYPE_PROPERTY, CLASS_TYPE);
	}

	public static Pattern forTypeReferencingNode(ASTNode sourceNode) {
		StructuralPropertyDescriptor parentProperty = sourceNode.getLocationInParent();
		if (parentProperty.equals(SingleVariableDeclaration.TYPE_PROPERTY)) {
			ASTNode grandParent = sourceNode.getParent().getParent();
			if (grandParent instanceof MethodDeclaration)
				return METHOD_PARAMETER;
			if (grandParent instanceof CatchClause)
				return CATCH_PARAMETER;
			if (grandParent instanceof EnhancedForStatement)
				return VARIABLE_TYPE;
		}

		if (parentProperty.equals(TypeDeclaration.SUPERCLASS_TYPE_PROPERTY)) {
			assert !((TypeDeclaration) sourceNode.getParent()).isInterface();
			return EXTENDS_CLASS;
		}

		if (parentProperty.equals(TypeDeclaration.SUPER_INTERFACE_TYPES_PROPERTY)) {
			TypeDeclaration parentTypeDeclaration = (TypeDeclaration) sourceNode.getParent();
			if (parentTypeDeclaration.isInterface())
				return EXTENDS_INTERFACE;
			else
				return IMPLEMENTS_INTERFACE;
		}

		Pattern p = type_mapping.get(parentProperty);
		if (p != null)
			return p;

		throw new Error("Don't know how to create forest Pattern for given type referencing node");
	}

	public static Pattern forFieldReferencingNode(ASTNode n, IVariableBinding f) {
		if (n instanceof SuperFieldAccess)
			return SUPER_FIELD_ACCESS;
		if (n instanceof Name || n instanceof FieldAccess) {
			if (Modifier.isStatic(f.getModifiers()))
				return STATIC_FIELD_ACCESS;
			else
				return INSTANCE_FIELD_ACCESS;
		}
		throw new Error("Don't know how to create forest Pattern for given field referencing node");
	}

	public static Pattern forMethodReferencingNode(ASTNode n, IMethodBinding mb) {
		if (n instanceof MethodInvocation)
			if (Modifier.isStatic(mb.getModifiers()))
				return STATIC_METHOD_CALL;
			else
				return INSTANCE_METHOD_CALL;
		if (n instanceof SuperConstructorInvocation)
			return SUPER_CONSTRUCTOR_CALL;
		if (n instanceof ConstructorInvocation)
			return CONSTRUCTOR_CALL;
		if (n instanceof SuperMethodInvocation)
			return SUPER_METHOD_CALL;
		throw new Error("Don't know how to create forest Pattern for given method referencing node");
	}
	

}
