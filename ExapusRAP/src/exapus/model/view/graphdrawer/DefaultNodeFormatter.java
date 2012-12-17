package exapus.model.view.graphdrawer;

import java.util.Collections;

import exapus.gui.editors.forest.graph.INode;
import exapus.gui.editors.forest.graph.INodeFormatter;
import exapus.model.forest.ForestElement;

public class DefaultNodeFormatter implements INodeFormatter {

	@Override
	public String label(INode n) {
		ForestElement fe = (ForestElement) n;
		return "\"" + fe.getName().toString() + "\"";
	}

	@Override
	public Iterable<String> decorations(INode n) {
		return Collections.emptyList();
	}

	@Override
	public String getIdentifier(INode n) {
		return Integer.toString(System.identityHashCode(n));
	}

}
