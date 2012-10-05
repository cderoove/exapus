package exapus.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;

public abstract class MemberContainer extends ForestElement {

	protected List<Member> members;

	public MemberContainer(UqName id) {
		super(id);
		members = new ArrayList<Member>(); // types
	}

	public Iterable<Member> getMembers() {
		return members;
	}

	Member getOrAddMember(UqName id, Element e) {
		for (Member m : members)
			if (m.getName().equals(id) && m.getElement().equals(e))
				return m;
		Member m = new Member(id, e);
		members.add(m);
		m.setParent(this);
		getParentFactForest().fireUpdate(m);
		return m;
	}

	Member getOrAddMember(Iterator<ASTNode> i) {
		ASTNode bd = i.next();
		UqName rootMemberName = UqName.forNode(bd);
		Element rootMemberElement = Element.forNode(bd);
		Member rootMember = getOrAddMember(rootMemberName, rootMemberElement);
		if (i.hasNext())
			return rootMember.getOrAddMember(i);
		else
			return rootMember;
	}

	Member getOrAddMember(UqName memberName, Element memberElement, Iterator<ASTNode> i) {
		if (i.hasNext())
			return getOrAddMember(i);
		else
			return getOrAddMember(memberName, memberElement);
	}


	protected Member getOrAddMemberWithoutRecursing(ITypeBinding typeBinding) {
		//top level member being added to a PackageLayer
		UqName memberName = UqName.forBinding(typeBinding);
		Element memberElement = Element.forBinding(typeBinding);
		return getOrAddMember(memberName,memberElement);
	}

	private Member getOrAddNestedMember(ITypeBinding typeBinding) {
		IMethodBinding declaringMethod = typeBinding.getDeclaringMethod();
		ITypeBinding declaringClass = typeBinding.getDeclaringClass();
		Member declaringTypeMember = getOrAddMember(declaringClass);
		if(declaringMethod != null) {
			Member declaringMethodMember = declaringTypeMember.getOrAddMember(declaringMethod);
			return declaringMethodMember.getOrAddMemberWithoutRecursing(typeBinding);
		} else
			return declaringTypeMember.getOrAddMemberWithoutRecursing(typeBinding);
	}

	public Member getOrAddMember(IMethodBinding methodBinding) {
		ITypeBinding declaringClass = methodBinding.getDeclaringClass();
		Member declaringMember = getOrAddMember(declaringClass);
		UqName memberName = UqName.forBinding(methodBinding);
		Element memberElement = Element.forBinding(methodBinding);
		return declaringMember.getOrAddMember(memberName, memberElement);
	}

	public Member getOrAddMember(IVariableBinding variableBinding) {
		ITypeBinding declaringClass = variableBinding.getDeclaringClass();
		Member declaringMember = getOrAddMember(declaringClass);
		UqName memberName = UqName.forBinding(variableBinding);
		Element memberElement = Element.forBinding(variableBinding);
		return declaringMember.getOrAddMember(memberName, memberElement);
	}

	public Member getOrAddMember(ITypeBinding typeBinding) {
		if(typeBinding.isNested()) 
			return getOrAddNestedMember(typeBinding);
		else
			return getOrAddMemberWithoutRecursing(typeBinding);
	}




}
