package exapus.model.view;

import exapus.model.forest.InboundRef;
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
	public String getNameString() {
		return "*";
	}	
	
	@Override
	public String getScopeString() {
		return "";
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

	@Override
	public boolean matchProjectPackageTree(PackageTree packageTree) {
		return true;
	}

	@Override
	public boolean matchProjectPackageLayer(PackageLayer packageLayer) {
		return true;
	}

	@Override
	public boolean matchProjectMember(Member member) {
		return true;
	}

	@Override
	public boolean matchAPIRef(InboundRef inboundRef) {
		return true;
	}

}
