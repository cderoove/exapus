package exapus.model.view;

import exapus.model.forest.InboundRef;
import exapus.model.forest.Member;
import exapus.model.forest.OutboundRef;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.Ref;

public abstract class Selection {

	public abstract boolean matchAPIPackageTree(PackageTree packageTree);

	public abstract boolean matchAPIPackageLayer(PackageLayer packageLayer);

	public abstract boolean matchAPIMember(Member member);

	public abstract boolean matchProjectPackageTree(PackageTree packageTree);

	public abstract boolean matchProjectPackageLayer(PackageLayer packageLayer);

	public abstract boolean matchProjectMember(Member member);
	
	public abstract boolean matchAPIRef(InboundRef inboundRef);
	
	public abstract boolean matchProjectRef(OutboundRef outboundRef);
	
	public abstract String getNameString();
	
	public abstract String getScopeString();
	
	public abstract String getTagString();
	
	
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
