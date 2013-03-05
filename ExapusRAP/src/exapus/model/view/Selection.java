package exapus.model.view;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import exapus.model.forest.ForestElement;
import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.Ref;
import exapus.model.tags.Tag;

@XmlRootElement
public abstract class Selection {
	
	public abstract boolean mayContainMatches(PackageTree packageTree);

	public abstract boolean mayContainMatches(PackageLayer packageLayer);

	public abstract boolean mayContainMatches(Member member);
	
	public abstract boolean matches(Ref ref);
	
	public abstract boolean matches(PackageTree packageTree);
	
	public abstract boolean matches(PackageLayer packageLayer);
	
	public abstract boolean matches(Member member);
	
	public boolean matches(ForestElement e) {
		if(e instanceof Ref)
			return matches((Ref) e);
		if(e instanceof Member)
			return matches((Member) e);
		if(e instanceof PackageLayer)
			return matches((PackageLayer) e);
		if(e instanceof PackageTree)
			return matches((PackageTree) e);
		return false;
	}

	public abstract String getNameString();
	
	public abstract String getScopeString();
	
	public String getTagString() {
		return (hasTag() ? getTag().toString() : "");
	}
	
	public abstract boolean hasTag();
	
	public abstract Tag getTag();
	
	
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
