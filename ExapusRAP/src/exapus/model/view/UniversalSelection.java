package exapus.model.view;

import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;

public class UniversalSelection extends Selection {
	
	private static UniversalSelection current = new UniversalSelection();
	
	public static UniversalSelection getCurrent() {
		return current;
	}
	
	private UniversalSelection() {
	}

	@Override
	public boolean matchAPIPackageTree(PackageTree packageTree) {
		return true;
	}

	@Override
	public boolean matchAPIPackageLayer(PackageLayer packageLayer) {
		return true;
	}

	@Override
	public boolean matchAPIMember(Member member) {
		return true;
	}

}
