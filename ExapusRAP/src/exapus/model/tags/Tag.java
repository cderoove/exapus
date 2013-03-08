package exapus.model.tags;

import com.google.common.base.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Tag implements Comparable<Tag> {

    private String identifier;
    private String parentName;
    private String subName;

    public Tag() {
        this("");
    }

    public Tag(String identifier) {
        this.identifier = identifier;
    }

    public Tag(String identifier, String parentName) {
        this.subName = identifier;
        this.parentName = parentName;
        this.identifier = String.format("%s::%s", parentName, identifier);
    }

    @XmlElement
    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    @XmlElement
    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String id) {
        this.identifier = id;

        if (id.contains("::")) {
            this.subName = id.substring(id.indexOf("::") + 2);
        }
    }

    @Override
    public String toString() {
        return this.identifier;
    }

    public String getLabelName() {
        if (isSuperTag()) return identifier;
        return subName;
    }

    public String toDebugString() {
        return String.format("id=%s, parent=%s, subname=%s", this.identifier, this.parentName, this.subName);
    }

/*
    private String fullName() {
        if (isSuperTag()) return getIdentifier();
        return String.format("%s::%s", this.parentName, this.identifier);
    }
*/

    @Override
    public int hashCode() {
        return Objects.hashCode(this.identifier);
    }

    @Override
    public boolean equals(Object other) {
        return Objects.equal(this.identifier, ((Tag) other).identifier);
    }

    @Override
    public int compareTo(Tag o) {
        return identifier.compareTo(o.identifier);
    }

    public boolean isSubTag() {
        return !isSuperTag();
    }

    public boolean isSuperTag() {
        return parentName == null || parentName.isEmpty();
    }

}
