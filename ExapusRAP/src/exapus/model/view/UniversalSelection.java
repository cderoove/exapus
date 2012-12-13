package exapus.model.view;

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

}
