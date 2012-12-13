package exapus.gui.views.forest.graph;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import javax.imageio.ImageIO;

import com.google.common.base.Joiner;

public class GraphViz {

	public static String DOT_EXC = "/usr/local/Cellar/graphviz/2.28.0/bin/dot";
	public static String IMG_EXT = "png";

	
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
	private void dotToImage(File dotfile, File imgfile) throws IOException {
		Runtime rt = Runtime.getRuntime();
		String[] cmdargs = {DOT_EXC, "-T"+IMG_EXT, dotfile.getAbsolutePath(), "-o", imgfile.getAbsolutePath()};
		Process p;
		try {
			p = rt.exec(cmdargs);
			p.waitFor();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
				
	}
	
	public BufferedImage toImage(IGraphFormatter gf, INodeFormatter nf, IEdgeFormatter ef) throws IOException {
		File dotfile = File.createTempFile("generateddot", "dot");
		toDotFile(dotfile,gf,nf,ef);
		File imgfile = File.createTempFile("converteddot", IMG_EXT);
		dotToImage(dotfile,imgfile);
		BufferedImage img = ImageIO.read(imgfile);
		dotfile.delete();
		imgfile.delete();
		return img;
	}



}
