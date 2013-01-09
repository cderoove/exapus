package exapus.model.view;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

import exapus.model.forest.QName;

public class ViewFactory {

	private static ViewFactory current;

	static {
		current = new ViewFactory();
	}

	public static ViewFactory getCurrent() {
		return current;
	}


	private View completePackageView;
	private View completeProjectView;


	private ViewFactory() {
		Selection universal = UniversalSelection.getCurrent();
		completePackageView = new APICentricView("All Packages");
		completePackageView.addAPISelection(universal);
		completePackageView.addProjectSelection(universal);
		completePackageView.setRenderable(false);

		completeProjectView = new ProjectCentricView("All Projects");
		completeProjectView.addAPISelection(universal);
		completeProjectView.addProjectSelection(universal);
		completeProjectView.setRenderable(false);


	}

	public View completePackageView() {
		return completePackageView;
	}

	public View completeProjectView() {
		return completeProjectView;
	}

	public View testAPICentricSelectionView() {
		View view = new APICentricView("API-centric selection test");
		view.addAPISelection(new ScopedSelection(new QName("java.lang.Integer"), Scope.TYPE_SCOPE));
		view.addAPISelection(new ScopedSelection(new QName("java.util.Iterator.hasNext()"), Scope.METHOD_SCOPE));
		view.addAPISelection(new ScopedSelection(new QName("javax"), Scope.PREFIX_SCOPE));
		view.addAPISelection(new ScopedSelection(new QName("org.apache.commons"), Scope.PREFIX_SCOPE));
		view.addAPISelection(new ScopedSelection(new QName("org.apache.tools.ant"), Scope.PACKAGE_SCOPE));
		view.addProjectSelection(UniversalSelection.getCurrent());
		view.setRenderable(false);

		return view;
	}

	public View testAPICentricSelectionView2() {
		View view = testAPICentricSelectionView();
		view.setName("API-centric selection test 2");
		view.removeProjectSelection(UniversalSelection.getCurrent());
		view.addProjectSelection(new ScopedSelection(new QName("org.sunflow"), Scope.PREFIX_SCOPE));
		view.addProjectSelection(new ScopedSelection(new QName("tomcat"), Scope.ROOT_SCOPE));		
		return view;
	}

	public View testProjectCentricSelectionView() {
		View view = new ProjectCentricView("Project-centric selection test");
		view.addProjectSelection(new ScopedSelection(new QName("sunflow"), Scope.ROOT_SCOPE));
		view.addAPISelection(new ScopedSelection(new QName("java.lang.String"), Scope.TYPE_SCOPE));
		view.setRenderable(false);
		return view;
	}


	public View viewFromCSVTags(File file) throws IOException {
		ImmutableList<Selection> selections = Files.readLines(file, Charsets.UTF_8,
				new LineProcessor<ImmutableList<Selection>>() {
			final ImmutableList.Builder<Selection> builder = ImmutableList.builder();

			@Override
			public boolean processLine(String line) throws IOException {
				String[] columns = Iterables.toArray(Splitter.on(';').trimResults().split(line), String.class);
				if(columns.length != 4)
					throw new IOException("Incorrect amount of columms for CSV line: " + line);
				String tag = columns[0];
				String prefix = columns[2];
				if(prefix.isEmpty()) 
					throw new IOException("Third column should contain a package prefix: " + line);
				int subsincluded = Integer.parseInt(columns[3]);
				ScopedSelection selection = new ScopedSelection(new QName(prefix));
				//from apis.csv README: prune bit: 0 do not include (i.e., select?) subs, 1 do include subs
				if(subsincluded == 0) 
					selection.setScope(Scope.PACKAGE_SCOPE);
				else if(subsincluded == 1)
					selection.setScope(Scope.PREFIX_SCOPE);
				else throw new IOException("Fourth column should be either 0 or 1: " + line);
				builder.add(selection);
				return true;
			}
			
			@Override
			public ImmutableList<Selection> getResult() {
				return builder.build();
			}
		});
		
		View view = new APICentricView("Tagged APIs");
		for(Selection selection : selections) {
			view.addAPISelection(selection);
		}
		return view;

	}




}
