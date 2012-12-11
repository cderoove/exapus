package exapus.gui.views.graph;

public class Edge {
	
	Edge(Node f, Node t) {
		from = f;
		to = t;
	}
	
	public Node getFrom() {
		return from;
	}

	public void setFrom(Node from) {
		this.from = from;
	}

	public Node getTo() {
		return to;
	}

	public void setTo(Node to) {
		this.to = to;
	}

	private Node from;
	
	private Node to;

}
