package exapus.model.forest;

import java.util.Iterator;
import java.util.List;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.hash.HashCode;

@XmlRootElement
public class QName {
	
	public QName() {
		this("");
	}

	public static QName forBinding(ITypeBinding apiType) {
		String binaryName = apiType.getBinaryName();
		if(binaryName != null)
			return new QName(binaryName);
		//probably for PPA-produced bindings
		String otherName = apiType.getQualifiedName();
		return new QName(otherName);
	}

	public static QName forMemberBinding(IVariableBinding fb, ITypeBinding tb) {
		String fieldName = fb.getName();
		String typeName = tb.getBinaryName();
		return new QName(typeName + "." + fieldName);
	}

	public static QName forMemberBinding(IMethodBinding mb, ITypeBinding tb) {
		String methodName = UqName.forBinding(mb).toString();
		String typeName = tb.getBinaryName();
		return new QName(typeName + "." + methodName);
	}
	
	private ArrayList<UqName> components;

	private String identifier;

	@XmlValue
	public String getIdentifier() {
		return identifier;
	}
	
	//only to be used by JAXB
	public void setIdentifier(String identifier) {
		 this.identifier = identifier;
		components = toQNameComponents(Splitter.on('.').split(identifier));
	}

	private static ArrayList<UqName> toQNameComponents(Iterable<String> i) {
		ArrayList<UqName >components = new ArrayList<UqName>();
		if (!(i.iterator().hasNext()))
			components.add(UqName.EMPTY);
		else
			for (String s : i)
				components.add(new UqName(s));
		return components;
	}
	
	public QName(Iterable<String> i) {
		components = toQNameComponents(i);
		identifier = Joiner.on('.').join(i);
	}
	
	public QName(IPackageFragment f) {
		this(f.getElementName());
	}

	public QName(String s) {
		this(Splitter.on('.').split(s));
	}

	public QName(UqName n) {
		this(n.toString());
	}
		
	public boolean hasMultipleComponents() {
		return components.size() > 1;
	}

	public List<UqName> getComponents() {
		return components;
	}

	public String toString() {
		return identifier;
	}

	// TODO: clean up equals/hashcode
	public boolean equals(Object o) {
		if(o instanceof QName) {
			QName other = (QName)  o;
			return identifier.equals(other.identifier);
		}
		return false;
	}

	public int hashCode() {
		return identifier.hashCode();
	}
	
	public boolean isPrefixOf(QName other) {
		Iterator<UqName> myComponents = components.iterator();
		Iterator<UqName> otherComponents = other.getComponents().iterator();
		while(myComponents.hasNext()) {
			UqName myComponent = myComponents.next();
			if(!otherComponents.hasNext())
				return false;
			UqName otherComponent = otherComponents.next();
			if(!myComponent.equals(otherComponent))
				return false;
		}
		return true;
	}
	
	public QName getButLast() {
		int size = components.size();
		if(size == 0)
			return null;
		return new QName(Joiner.on('.').join(components.subList(0, size - 1)));		
	}
	

}
