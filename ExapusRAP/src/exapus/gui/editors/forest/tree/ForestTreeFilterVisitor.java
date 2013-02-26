package exapus.gui.editors.forest.tree;


import java.util.regex.Pattern;

import exapus.model.forest.InboundRef;
import exapus.model.forest.Member;
import exapus.model.forest.OutboundRef;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.Ref;
import exapus.model.visitors.CopyingForestVisitor;

public class ForestTreeFilterVisitor extends CopyingForestVisitor {

	private String apiFilter = ".*";
	private String projectFilter = ".*";


	
	private static String toSearchString(String s) {
		return ".*" + Pattern.quote(s.trim()) + ".*";
	}

	public void setApiFilter(String apiFilter) {
		this.apiFilter = toSearchString(apiFilter);
	}

	public void setProjectFilter(String projectFilter) {
		this.projectFilter = toSearchString(projectFilter);
	}

	protected boolean selectRef(Ref ref) {
		String apiText = ref.getReferencedName().toString();
		String projectText = ref.getReferencingName().toString();
		if(apiText.matches(apiFilter) && projectText.matches(projectFilter)) {
			PackageTree destinationTree = getCopy().getOrAddPackageTree(ref.getParentPackageTree().getName());
			destinationTree.copyReference(ref);
			return true;
		}
		return false;
	}

	@Override
	public boolean visitPackageTree(PackageTree packageTree) {
		return true;
	}

	@Override
	public boolean visitPackageLayer(PackageLayer packageLayer) {
		return true;
	}

	@Override
	public boolean visitMember(Member member) {
		return true;
	}

	@Override
	public boolean visitInboundReference(InboundRef inboundRef) {
		return selectRef(inboundRef);
	}

	@Override
	public boolean visitOutboundReference(OutboundRef outboundRef) {
		return selectRef(outboundRef);
	}



}
