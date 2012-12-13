package exapus.gui.editors.forest.graph;

public interface INodeFormatter {

	abstract public String label(Node n);
	
	abstract public Iterable<String> decorations(Node n);

}

	
