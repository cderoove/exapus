package exapus.model.forest;

import java.util.UUID;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.SourceRange;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import com.google.common.base.Objects;

import exapus.model.visitors.IForestVisitor;

public abstract class Ref extends ForestElement {

	private Ref dual;

	protected Direction direction;

	protected Pattern pattern;

	protected Element element;

	protected QName rname; // list of uqnames to be taken through tree

	protected SourceRange range;

	private int lineNumber;

	public Ref(Direction d, Pattern p, Element e, QName n, SourceRange r, int l) {
		// super(new UqName(UUID.randomUUID().toString())); //probably too slow
		super(UqName.EMPTY);
		direction = d;
		pattern = p;
		element = e;
		rname = n;
		range = r;
		lineNumber = l;
	}

	@Override
	public ICompilationUnit getCorrespondingICompilationUnit() {
		return getParentMember().getCorrespondingICompilationUnit();
	}

	public String getSourceString() {
		Member parent = getParentMember();
		if(parent == null)
			return null;
		return parent.getSourceString();
	}

	public int getSourceCharacterIndexOffset() {
		Member parent = getParentMember();
		if(parent == null)
			return 0;
		return parent.getSourceCharacterIndexOffset();
	}

	public int getSourceLineNumberOffset() {
		Member parent = getParentMember();
		if(parent == null)
			return 0;
		return parent.getSourceLineNumberOffset();
	}

	protected static int getLineNumber(ASTNode n) {
		int startPosition = n.getStartPosition();
		CompilationUnit cu = (CompilationUnit) n.getRoot();
		return cu.getLineNumber(startPosition);
	}

	protected static SourceRange getSourceRange(ASTNode n) {
		return new SourceRange(n.getStartPosition(), n.getLength());
	}

	static public Ref fromRef(Ref r) {
		if(r instanceof InboundRef)
			return InboundRef.fromInboundRef((InboundRef) r);
		else
			return OutboundRef.fromOutboundRef((OutboundRef) r);
	}

	public SourceRange getSourceRange() {
		return range;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public Pattern getReferencingPattern() {
		return pattern;
	}


	protected Element getElementOfParentMember() {
		Member parentMember = getParentMember();
		if(parentMember == null)
			return null;
		return parentMember.getElement();
	}



	public abstract QName getReferencingName();

	public abstract QName getReferencedName();

	public abstract Element getReferencingElement();

	public abstract Element getReferencedElement();

	@Override
	public QName getQName() {
		Member parentMember = getParentMember();
		if(parentMember == null)
			return null;
		return parentMember.getQName();
	}

	public Ref getDual() {
		return dual;
	}

	public void setDual(Ref dual) {
		this.dual = dual;
	}

	abstract public void acceptVisitor(IForestVisitor v);

	boolean equals(Ref ref) {
		if (ref == null)
			return false;
		if(ref == this)
			return true;
		return ref.direction == direction
				&& ref.pattern == pattern
				&& ref.element == element
				&& ref.range.equals(range)
				&& ref.lineNumber == lineNumber
				&& ref.rname.equals(rname);
	}

}

