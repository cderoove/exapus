package exapus.gui.editors.forest.graph;

public class Edge {
	
	public Edge(INode f, INode t) {
		from = f;
		to = t;
	}
	
	public INode getFrom() {
		return from;
	}

	public void setFrom(INode from) {
		this.from = from;
	}

	public INode getTo() {
		return to;
	}

	public void setTo(INode to) {
		this.to = to;
	}

	private INode from;
	
	private INode to;

}
