package exapus.model.tags;

import com.google.common.base.Objects;
import exapus.model.view.Scope;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Tag implements Comparable<Tag> {

    public static enum RELATION {
        NONE, CHILD, DOMAIN;

        public static RELATION[] supportedRelations(Scope scope) {
            if (scope == null) return new RELATION[] {NONE};

            switch (scope) {
                case TAG_SCOPE:
                    return new RELATION[]{DOMAIN, NONE};
                case ROOT_SCOPE:
                case PREFIX_SCOPE:
                case PACKAGE_SCOPE:
                case TYPE_SCOPE:
                case METHOD_SCOPE:
                    return new RELATION[]{CHILD, NONE};
                default:
                    return new RELATION[]{NONE};
            }
        }
    }

    private String identifier;
    private String associatedName;
    private RELATION relation;
    private String displayName;

    public Tag() {
        this("");
    }

    public Tag(String identifier) {
        this.identifier = identifier.intern();
    }

    public Tag(String identifier, String associatedName, RELATION relation) {
        this.identifier = identifier.intern();
        this.associatedName = associatedName.intern();
        this.relation = relation;
    }

    @XmlElement
    public String getRelation() {
        return relation.name();
    }

    public void setRelation(String relation) {
        if (relation == null || relation.isEmpty()) {
            this.relation = RELATION.NONE;
        } else {
            this.relation = RELATION.valueOf(relation);
        }
    }

    @XmlElement
    public String getAssociatedName() {
        return associatedName;
    }

    public void setAssociatedName(String associatedName) {
        this.associatedName = associatedName;
    }

    @XmlElement
    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String id) {
        this.identifier = id;
    }

    @Override
    public String toString() {
        return this.identifier;
    }

    @XmlElement
    public String getDisplay() {
        if (displayName == null || displayName.isEmpty()) return identifier;
        return displayName;
    }

    public void setDisplay(String displayName) {
        if (displayName == null || displayName.isEmpty()) {
            this.displayName = identifier;
        } else {
            this.displayName = displayName;
        }
    }

    public String toDebugString() {
        //return String.format("id=%s, parent=%s, subname=%s", this.identifier, this.associatedName, this.subName);
        return String.format("id=%s, aName=%s, relation=%s", this.identifier, this.associatedName, this.relation);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.identifier) + Objects.hashCode(this.associatedName);
    }

    @Override
    public boolean equals(Object other) {
        return Objects.equal(this.identifier, ((Tag) other).identifier) && Objects.equal(this.associatedName, ((Tag) other).associatedName);
    }

    @Override
    public int compareTo(Tag o) {
        return identifier.compareTo(o.identifier);
    }

    public boolean isSubTag() {
        return RELATION.CHILD.equals(this.relation);
    }

    public boolean isSuperTag() {
        return !isSubTag();
    }

    public boolean isDomain() {
        return RELATION.DOMAIN.equals(relation);
    }

}
