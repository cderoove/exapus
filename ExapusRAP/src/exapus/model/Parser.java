package exapus.model;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class Parser {
	
	public static CompilationUnit[] parse(IJavaProject p, ICompilationUnit[] icus, IProgressMonitor monitor) {
		ASTParser parser = ASTParser.newParser(AST.JLS4); // seems better than
		// reusing the
		// existing one (ran
		// out of memory on
		// azureus
		// otherwise)

		final CompilationUnit[] compilationUnits = new CompilationUnit[icus.length];

		/*
		 * ASTRequestor requestor = new ASTRequestor() { private int current=0;
		 * public void acceptAST(ICompilationUnit source, CompilationUnit ast){
		 * compilationUnits[current++] = ast; }; };
		 */

		// Normally, would parse entire batch of ICompilationUnits
		// But there is an annoying known bug:
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=359478
		// http://stackoverflow.com/questions/7603096/why-am-i-getting-nullpointerexception-in-the-compilationunit-instances-returned
		// parser.createASTs(icus, new String[0], requestor, monitor);
		// The following is a lot slower, but seems to work fine
		// TODO: upon new JDT release, check whether bug has been resolved
		int i = 0;
		for (ICompilationUnit icu : icus) {
			parser.setResolveBindings(true);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setProject(p);
			parser.setStatementsRecovery(false);
			parser.setBindingsRecovery(false);
			parser.setSource(icu);
			compilationUnits[i++] = (CompilationUnit) parser.createAST(null);
		}

		return compilationUnits;
	}


}
