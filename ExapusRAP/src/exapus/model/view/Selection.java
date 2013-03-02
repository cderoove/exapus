package exapus.model.view;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import exapus.model.forest.ForestElement;
import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.Ref;

@XmlRootElement
public abstract class Selection {

	public abstract boolean matchForestElement(ForestElement element);
	
	public abstract boolean match(PackageTree packageTree);

	public abstract boolean match(PackageLayer packageLayer);

	public abstract boolean match(Member member);
	
	public abstract boolean match(Ref ref);
		
	public abstract String getNameString();
	
	public abstract String getScopeString();
	
	public abstract String getTagString();
	
	public abstract boolean hasTag();
	
	
	private static Class<?>[] supportedSelections = {UniversalSelection.class, ScopedSelection.class};
	public static Object supportedSelections() {
		return supportedSelections;
	}
	
	public static Selection fromSelection(Selection sel) {
		if(sel instanceof UniversalSelection)
			return sel;
		if(sel instanceof ScopedSelection) {
			ScopedSelection original = (ScopedSelection) sel;
			return new ScopedSelection(original.getQName(), original.getScope());
		}
		return null;
	}



	
}
