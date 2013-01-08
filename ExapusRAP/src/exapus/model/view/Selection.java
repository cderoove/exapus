package exapus.model.view;

import exapus.model.forest.InboundRef;
import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;

public abstract class Selection {

	public abstract boolean matchAPIPackageTree(PackageTree packageTree);

	public abstract boolean matchAPIPackageLayer(PackageLayer packageLayer);

	public abstract boolean matchAPIMember(Member member);

	public abstract boolean matchProjectPackageTree(PackageTree packageTree);

	public abstract boolean matchProjectPackageLayer(PackageLayer packageLayer);

	public abstract boolean matchProjectMember(Member member);
	
	public abstract boolean matchAPIRef(InboundRef inboundRef);

	public abstract String getNameString();
	
	public abstract String getScopeString();
	
	
	private static Class<?>[] supportedSelections = {UniversalSelection.class, ScopedSelection.class};
	public static Object supportedSelections() {
		return supportedSelections;
	}


	
}
