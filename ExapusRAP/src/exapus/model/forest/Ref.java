package exapus.model.forest;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.SourceRange;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import com.google.common.collect.Multiset;

import exapus.model.store.Store;
import exapus.model.tags.Cloud;
import exapus.model.tags.Tag;
import exapus.model.visitors.IForestVisitor;

public abstract class Ref extends ForestElement {

	private Ref dual;
	
    private Cloud updatedDualTags;

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
		updatedDualTags = Cloud.EMPTY_CLOUD;
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

	static public Ref from(Ref r) {
		Ref ref = null;
		if(r instanceof InboundRef)
			ref = InboundRef.fromInboundRef((InboundRef) r);
		else
			ref = OutboundRef.fromOutboundRef((OutboundRef) r);
		ref.copyTagsFrom(r);
		ref.copyUpdatedDualTagsFrom(r);
		return ref;
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

	@Override
	public Cloud getDualTags() {
		return updatedDualTags;
	}
	
    public boolean copyDualTagsFromDual(Ref dual) {
    	Cloud before = updatedDualTags;
    	updatedDualTags = Store.getCurrent().getOrRegisterExtendedCloud(updatedDualTags, dual.getTags());
    	return updatedDualTags != before;
    }
    
    public boolean addDualTag(Tag tag) {
    	Cloud before = updatedDualTags;
    	updatedDualTags = Store.getCurrent().getOrRegisterExtendedCloud(updatedDualTags, tag);
    	return updatedDualTags != before;
    }
    
	private void copyUpdatedDualTagsFrom(Ref r) {
		updatedDualTags = r.updatedDualTags;
	}


	abstract public void acceptVisitor(IForestVisitor v);

	public boolean equals(Object other) {
		if(other == null)
			return false;
		if(other instanceof Ref) {
			Ref ref = (Ref) other;
			return ref.lineNumber == lineNumber
					&& ref.pattern == pattern
					&& ref.element == element
					&& ref.direction == direction
					&& ref.range.equals(range)
					&& ref.rname.equals(rname);
		}
		return false;
	}

    public Multiset<String> getAllTags() {
        return getTags().toMultiset();
    }

    public Multiset<String> getAllDualTags() {
        return getDualTags().toMultiset();
    }

	@Override
	public ForestElement getCorrespondingForestElement(ForestElement ancestor) {
		return null;
	}
	
	@Override
	public ForestElement getCorrespondingForestElement(boolean copyWhenMissing, ForestElement ancestor) {
		return null;
	}
	
	@Override
	public boolean hasChildren() {
		return false;
	}

	public Pattern getPattern() {
		return pattern;
	}
	
	public Element getElement() {
		return element;
	}

	


}

