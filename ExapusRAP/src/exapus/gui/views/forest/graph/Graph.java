package exapus.gui.views.forest.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Iterables;

import exapus.model.forest.Member;
import exapus.model.forest.Ref;


public class Graph {
	
	private Map<Node, List<Edge>> node2edges;
	
	public Graph() {
		clean();
	}
	
	public Iterable<Node> getNodes() {
		return node2edges.keySet();
	}
	
	public Iterable<Edge> getEdges() {
		Iterable<Edge> edges = new LinkedList<Edge>();
		for(List<Edge> e : node2edges.values()) {
			edges = Iterables.concat(edges, e);
		}
		return edges;
	}
	
	public List<Edge> getEdges(Node n) {
		return node2edges.get(n);
	}
	
	public void add(Node n) {
		ensureExistance(n);
	}
	
	public void add(Edge e) {
		ensureExistance(e.getFrom());
		getEdges(e.getFrom()).add(e);
	}
	
	
	private void clean() {
		 node2edges = new HashMap<Node, List<Edge>>();
	}
	
	private void ensureExistance(Node n) {
		if(node2edges.containsKey(n))
			return;
		node2edges.put(n, new LinkedList<Edge>());
	}


}
