package exapus.model.tags;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import com.google.common.base.Objects;

@XmlRootElement
public class Tag implements Comparable<Tag> {

	private String identifier;
	
	public Tag() {
		this("");
	}

	public Tag(String identifier) {
		this.identifier = identifier;
	}
	
	@XmlValue
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
	
	@Override
	public int hashCode(){
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
	
}
