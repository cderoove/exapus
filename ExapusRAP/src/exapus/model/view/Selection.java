package exapus.model.view;

import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;

public abstract class Selection {

	public abstract boolean matchAPIPackageTree(PackageTree packageTree);

	public abstract boolean matchAPIPackageLayer(PackageLayer packageLayer);

	public abstract boolean matchAPIMember(Member member);
	
}
