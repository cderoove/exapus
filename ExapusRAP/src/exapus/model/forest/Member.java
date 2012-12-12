package exapus.model.forest;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import com.google.common.collect.Iterables;

import exapus.model.visitors.IForestVisitor;

public class Member extends MemberContainer {

	public Member(UqName id, Element e) {
		super(id);
		element = e;
		references = new ArrayList<Ref>();
	}


	private List<Ref> references;

	private Element element;

	public Element getElement() {
		return element;
	}

	public Iterable<Ref> getReferences() {
		return references;
	}

	public Iterable<Ref> getAllReferences() {
		Iterable<Ref> references = getReferences();
		for(Member m : getMembers()) {
			references = Iterables.concat(references, m.getAllReferences());
		}
		return references;
	}
	
	public void addAPIReference(Ref reference) {
		references.add(reference);
		reference.setParent(this);
		getParentFactForest().fireUpdate(reference);
	}

	public String toString() {
		return "M[" + element.toString() + ": " + getName() + "(" + references.size() + ")" + "]";
	}

	public Member getTopLevelMember() {
		Member parent = this;
		Member oldParent = this;
		while (parent instanceof Member) {
			oldParent = parent;
			parent = parent.getParentMember();
		}
		return oldParent;
	}

	public ICompilationUnit getCorrespondingICompilationUnit() {
		IMember member = getCorrespondingIMember();
		if (member != null)
			return member.getCompilationUnit();
		return null;
	}

	public IMember getCorrespondingIMember() {
		//TODO: only if element = class/etc, or change to getCorrespondingMember
		//en daarna getMethod, getField ..
		IJavaProject project = getCorrespondingJavaProject();
		try {
			if (element.declaresType()) 
				return project.findType(getQName().toString(), (IProgressMonitor) null);

			Member declaringMember = getParentTypeDeclaringMember();
			if(declaringMember == null) 
				return null;

			IMember declaringIMember = declaringMember.getCorrespondingIMember();
			if(declaringIMember instanceof IType) {
				IType declaringIType = (IType) declaringIMember;
				if(element.isField())
					return declaringIType.getField(this.getName().toString());
				if(element.isMethod()) { 
					UqName uqName = getName();
					String shortName = uqName.getMethodName();
					String[] parameterSignatures  = uqName.getMethodParameterTypeSignatures();
					IMethod method = declaringIType.getMethod(shortName, parameterSignatures);
					return method;
				}					
				return null;
			}
			return null;
		} catch (JavaModelException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getSourceString() {
		IMember element = getCorrespondingIMember();
		if (element == null)
			return null;
		try {
			return element.getSource();
		} catch (JavaModelException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int getSourceCharacterIndexOffset() {
		IMember element = getCorrespondingIMember();
		if (element == null)
			return 0;
		try {
			return element.getSourceRange().getOffset();
		} catch (JavaModelException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public int getSourceLineNumberOffset() {
		IMember element = getCorrespondingIMember();
		if (element == null)
			return 0;
		try {
			int characterIndexOffset = element.getSourceRange().getOffset();
			ICompilationUnit icu = element.getCompilationUnit();
			IJavaProject ip = icu.getJavaProject();
			ICompilationUnit[] icus = {icu};
			//TODO: silly to reparse for this, but the usual IDocument means are unavailable (as RAP is incompatible with org.eclipse.jface.text)
			return Parser.parse(ip, icus, null)[0].getLineNumber(characterIndexOffset);
			
		} catch (JavaModelException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public void acceptVisitor(IForestVisitor v) {
		if(v.visitMember(this)) {
			for(Ref r : getReferences())
				r.acceptVisitor(v);
			for(Member m : getMembers())
				m.acceptVisitor(v);
		}
	}

}
