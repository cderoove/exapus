package exapus.model.forest;

import org.eclipse.jdt.core.SourceRange;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Type;

import exapus.model.visitors.IForestVisitor;

public class OutboundRef extends Ref {

	public static OutboundRef fromNodeToBinding(ASTNode n, ITypeBinding apiType) {
		Element referencedElement = Element.forBinding(apiType);
		QName referencedName = QName.forBinding(apiType);
		Pattern referencingPattern = Pattern.forTypeReferencingNode(n);
		return new OutboundRef(referencingPattern, referencedElement, referencedName, getSourceRange(n), getLineNumber(n));
	}

	public static OutboundRef fromNodeToBinding(ASTNode n, IVariableBinding fb, ITypeBinding tb) {
		Element referencedElement = Element.forBinding(fb);
		QName referencedName = QName.forMemberBinding(fb, tb);
		Pattern referencingPattern = Pattern.forFieldReferencingNode(n, fb);
		return new OutboundRef(referencingPattern, referencedElement, referencedName, getSourceRange(n), getLineNumber(n));
	}

	public static OutboundRef fromNodeToBinding(ASTNode n, IMethodBinding mb, ITypeBinding tb) {
		Element referencedElement = Element.forBinding(mb);
		QName referencedName = QName.forMemberBinding(mb, tb);
		Pattern referencingPattern = Pattern.forMethodReferencingNode(n, mb);
		return new OutboundRef(referencingPattern, referencedElement, referencedName, getSourceRange(n), getLineNumber(n));
	}
	
	
	public static OutboundRef fromOutboundRef(OutboundRef outboundRef) {
		OutboundRef copy = new OutboundRef(outboundRef.getReferencingPattern(), outboundRef.getReferencedElement(), outboundRef.getReferencedName(), outboundRef.getSourceRange(), outboundRef.getLineNumber());
		copy.setDual(outboundRef.getDual());
		return copy;
	}

	public OutboundRef(Pattern referencingPattern, Element referencedElement, QName referencedName, SourceRange r, int l) {
		super(Direction.OUTBOUND, referencingPattern, referencedElement, referencedName, r, l);
	}
	
	
	public QName getReferencingName() {
		return this.getQName();
	}
	
	public QName getReferencedName() {
		return rname;
	}
	
	public Element getReferencingElement() {
		return super.getElementOfParentMember();
	}
	
	public InboundRef toInboundRef() {
		return InboundRef.fromOutboundRef(this);
	}

	public Element getReferencedElement() {
		return element;
	}

	public String toString() {
		return "OR[" + getReferencingPattern().toString() + "->" + getReferencedElement().toString() + ":" + getReferencedName().toString() + "]";
	}

	@Override
	public void acceptVisitor(IForestVisitor v) {
			v.visitOutboundReference(this);
	}


	
	
}
