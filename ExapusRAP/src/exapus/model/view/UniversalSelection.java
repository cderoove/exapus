package exapus.model.view;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import exapus.model.forest.FactForest;
import exapus.model.forest.ForestElement;
import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.Ref;
import exapus.model.tags.Tag;

@XmlRootElement
public class UniversalSelection extends Selection {
	
	private static UniversalSelection current = new UniversalSelection();
	
	public static UniversalSelection getCurrent() {
		return current;
	}
	
	public UniversalSelection() {
		//only to be used by JAXB
	}
	
	@Override
	public String getNameString() {
		return "*";
	}	
	
	@Override
	public String getScopeString() {
		return "";
	}
	
	@Override
	public boolean hasTag() {
		return false;
	}

	@Override
	public boolean mayContainMatches(PackageTree packageTree) {
		return true;
	}

	@Override
	public boolean mayContainMatches(PackageLayer packageLayer) {
		return true;
	}

	@Override
	public boolean mayContainMatches(Member member) {
		return true;
	}

	@Override
	public boolean matches(Ref ref, FactForest source) {
		return true;
	}

	@Override
	public boolean matches(PackageTree packageTree, FactForest source) {
		return true;
	}

	@Override
	public boolean matches(PackageLayer packageLayer, FactForest source) {
		return true;
	}

	@Override
	public boolean matches(Member member, FactForest source) {
		return true;
	}

	@Override
	public boolean matches(ForestElement e, FactForest source) {
		return true;
	}

	@Override
	public Tag getTag() {
		return null;
	}



}
