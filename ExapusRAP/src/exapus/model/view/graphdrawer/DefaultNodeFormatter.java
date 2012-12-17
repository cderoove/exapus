package exapus.model.view.graphdrawer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exapus.gui.editors.forest.graph.INode;
import exapus.gui.editors.forest.graph.INodeFormatter;
import exapus.model.forest.Element;
import exapus.model.forest.ForestElement;
import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;

public class DefaultNodeFormatter implements INodeFormatter {

	@Override
	public String label(INode n) {
		ForestElement fe = (ForestElement) n;
		return "\"" + fe.getName().toString() + "\"";
	}

	@Override
	public Iterable<String> decorations(INode n) {
		List<String> decorations = new ArrayList<String>();
		if (n instanceof PackageLayer) {
			decorations.add("shape=oval");
			PackageLayer l = (PackageLayer) n;
			if(!l.hasMembers())
				decorations.add("style=\"dashed\"");
			return decorations;
		}
		if(n instanceof PackageTree) {
			decorations.add("shape=oval");
			decorations.add("style=\"dashed\"");
			return decorations;
		}

		if (n instanceof Member) {
			Member m = (Member) n;
			Element e = m.getElement();
			if(e.declaresType())
				decorations.add("shape=egg");
			if(e.isMethod())
				decorations.add("shape=box");
			if(e.isField())
				decorations.add("shape=parallelogram");
			return decorations;
		}
		
		return decorations;
	}

	@Override
	public String getIdentifier(INode n) {
		return Integer.toString(System.identityHashCode(n));
	}

}
