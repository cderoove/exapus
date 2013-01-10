package exapus.model.view;

import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.Ref;

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
	public String getTagString() {
		return "";
	}

	@Override
	public boolean hasTag() {
		return true;
	}

	@Override
	public boolean matchPackageTree(PackageTree packageTree) {
		return true;
	}

	@Override
	public boolean matchPackageLayer(PackageLayer packageLayer) {
		return true;
	}

	@Override
	public boolean matchMember(Member member) {
		return true;
	}

	@Override
	public boolean matchRef(Ref ref) {
		return true;
	}


}
