package exapus.gui.editors.forest.graph;

public interface INodeFormatter {

	abstract public String label(INode n);
	
	abstract public Iterable<String> decorations(INode n);

	abstract public String getIdentifier(INode n);

}

	
