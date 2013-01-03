package exapus.gui.editors.forest.graph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.google.common.base.Joiner;
import exapus.model.store.Store;

public class GraphViz {

	//TODO: SVG for clickable URLs in a graph, however: Chrome does not seem to render this well
	//public static String IMG_EXT = "svg";
	//public static String IMG_MIME = "image/svg+xml";

	public static String IMG_EXT = "png";
	public static String IMG_MIME = "image/png";

	private Graph graph;

	public GraphViz(Graph g) {
		graph = g;
	}

	public void writeNodes(Writer w, INodeFormatter f) throws IOException {
		for(INode n : graph.getNodes()) {
			w.write("\"");
			w.write(f.getIdentifier(n));
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

	public void writeEdges(Writer w, INodeFormatter nf, IEdgeFormatter f) throws IOException {
		for(Edge e : graph.getEdges()) {
			w.write("\"");
			w.write(nf.getIdentifier(e.getFrom()));
			w.write("\"");
			w.write(" -> ");
			w.write("\"");
			w.write(nf.getIdentifier(e.getTo()));
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
		writeEdges(w,nf,ef);
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

	private void dotToImage(File dotfile, File imgfile) throws IOException {
		Runtime rt = Runtime.getRuntime();
		String[] cmdargs = {Store.Settings.DOT_EXC.getValue(), "-T" + IMG_EXT, dotfile.getAbsolutePath(), "-o", imgfile.getAbsolutePath()};
		Process p;
		try {
			p = rt.exec(cmdargs);
			p.waitFor();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public File toImage(IGraphFormatter gf, INodeFormatter nf, IEdgeFormatter ef) throws IOException {
		File dotfile = File.createTempFile("generateddot", ".dot");
		toDotFile(dotfile,gf,nf,ef);
		File imgfile = File.createTempFile("converteddot", "." + IMG_EXT);
		dotToImage(dotfile,imgfile);
		//BufferedImage img = ImageIO.read(imgfile);
		dotfile.delete();
		//imgfile.delete();
		return imgfile;
		//return img;
	}



}
