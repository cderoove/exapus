package exapus.gui.views.graph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import com.google.common.base.Joiner;

public class GraphViz {

	public static String DOT_EXC = "/usr/local/Cellar/graphviz/2.28.0/bin/dot";
	public static String IMG_EXT = "svg";

	
	private Graph graph;

	public GraphViz(Graph g) {
		graph = g;
	}

	public void writeNodes(Writer w, INodeFormatter f) throws IOException {
		for(Node n : graph.getNodes()) {
			w.write("\"");
			w.write(n.getIdentifier());
			w.write("\"");
			w.write("[");
			String label = f.label(n);
			Iterable<String> decorations = f.decorations(n);
			if(label != null) {
				w.write("label=");
				w.write(label);
				if(decorations.iterator().hasNext()) 
					w.write(",");
			}
			w.write(Joiner.on(",").join(decorations));
			w.write("];");
			w.write("\n");
		}
	}

	public void writeEdges(Writer w, IEdgeFormatter f) throws IOException {
		for(Edge e : graph.getEdges()) {
			w.write("\"");
			w.write(e.getFrom().getIdentifier());
			w.write("\"");
			w.write(" -> ");
			w.write("\"");
			w.write(e.getTo().getIdentifier());
			w.write("\"");			
			w.write("[");
			Iterable<String> decorations = f.decorations(e);
			if(decorations.iterator().hasNext())
				w.write(Joiner.on(",").join(decorations));
			w.write("];");
			w.write("\n");
		}
	}

	public void writePrologue(Writer w, IGraphFormatter f) throws IOException {
		w.write("digraph {\n");
		w.write("graph [");
		Iterable<String> decorations = f.decorations(graph);
		if(decorations.iterator().hasNext())
			w.write(Joiner.on(",").join(decorations));
		w.write("];\n");
	}


	public void writeEpilogue(Writer w) throws IOException {
		w.write("}\n");
	}

	public void write(Writer w, IGraphFormatter gf, INodeFormatter nf, IEdgeFormatter ef) throws IOException {
		writePrologue(w, gf);
		writeNodes(w,nf);
		writeEdges(w,ef);
		writeEpilogue(w);
	}

	public void toDotFile(File dotfile ,IGraphFormatter gf, INodeFormatter nf, IEdgeFormatter ef) throws IOException {
		FileWriter w = new FileWriter(dotfile);
		try {
			write(w, gf, nf, ef);
		} finally {
			w.close();
		}
	}
	
	//TODO: file.createTemp file 
	private void dotToSVG(File dotfile, File svgfile) throws IOException {
		Runtime rt = Runtime.getRuntime();
		String[] cmdargs = {DOT_EXC, "-T"+IMG_EXT, dotfile.getAbsolutePath(), "-o", svgfile.getAbsolutePath()};
		Process p;
		try {
			p = rt.exec(cmdargs);
			p.waitFor();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
				
	}
	
	public void toSVGFile(IGraphFormatter gf, INodeFormatter nf, IEdgeFormatter ef) throws IOException {
		File dotfile = new File("/Users/cderoove/Desktop/lookatme.dot");
		toDotFile(dotfile,gf,nf,ef);
		File svgfile = new File("/Users/cderoove/Desktop/lookatme.svg");
		dotToSVG(dotfile, svgfile);
	}



}
