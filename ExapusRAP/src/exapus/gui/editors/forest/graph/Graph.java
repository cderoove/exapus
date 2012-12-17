package exapus.gui.editors.forest.graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Iterables;


public class Graph {
	
	
	
	private Map<INode, List<Edge>> node2edges;
	
	public Graph() {
		clean();
	}
	
	public Iterable<INode> getNodes() {
		return node2edges.keySet();
	}
	
	public Iterable<Edge> getEdges() {
		Iterable<Edge> edges = new LinkedList<Edge>();
		for(List<Edge> e : node2edges.values()) {
			edges = Iterables.concat(edges, e);
		}
		return edges;
	}
	
	public List<Edge> getEdges(INode n) {
		return node2edges.get(n);
	}
	
	public void add(INode n) {
		ensureExistance(n);
	}
	
	public void add(Edge e) {
		ensureExistance(e.getFrom());
		ensureExistance(e.getTo());
		getEdges(e.getFrom()).add(e);
	}
	
	
	private void clean() {
		 node2edges = new HashMap<INode, List<Edge>>();
	}
	
	private void ensureExistance(INode n) {
		if(node2edges.containsKey(n))
			return;
		node2edges.put(n, new LinkedList<Edge>());
	}


}
