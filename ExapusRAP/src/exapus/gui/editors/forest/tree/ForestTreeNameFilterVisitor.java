package exapus.gui.editors.forest.tree;


import java.util.regex.Pattern;

import exapus.model.forest.ForestElement;
import exapus.model.forest.InboundRef;
import exapus.model.forest.Member;
import exapus.model.forest.OutboundRef;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;
import exapus.model.forest.Ref;
import exapus.model.visitors.CopyingForestVisitor;

public class ForestTreeNameFilterVisitor extends ForestTreeFilterVisitor {

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
	
	@Override
	protected boolean copyRef(Ref ref) {
		String apiText = ref.getReferencedName().toString();
		String projectText = ref.getReferencingName().toString();
		if(apiText.matches(apiFilter) && projectText.matches(projectFilter)) {
			forestCopy.getCorrespondingForestElement(true, ref);
			return true;
		}
		return false;
	}

}
