package exapus.gui.editors.forest.graph;

public class Node {

	public Node() {}
		
	public Node(String id) {
		identifier = id;
	}
	
	private String identifier = null;
	
	
	
	public String getIdentifier() {
		return (identifier == null ? Integer.toString(System.identityHashCode(this)) : identifier);
	}
	
	
	
}
