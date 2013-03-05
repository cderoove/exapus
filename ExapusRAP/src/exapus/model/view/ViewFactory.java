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
import exapus.model.store.Store;
import exapus.model.tags.Tag;

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
		completePackageView = new View("All APIs", Perspective.API_CENTRIC);
		completePackageView.addAPISelection(universal);
		completePackageView.addProjectSelection(universal);
		completePackageView.setRenderable(true);
		completePackageView.seal();

		completeProjectView = new View("All Projects", Perspective.PROJECT_CENTRIC);
		completeProjectView.addAPISelection(universal);
		completeProjectView.addProjectSelection(universal);
		completeProjectView.setRenderable(true);
		completeProjectView.seal();


	}

	public View completePackageView() {
		return completePackageView;
	}

	public View completeProjectView() {
		return completeProjectView;
	}

	public View testAPICentricSelectionView() {
		View view = new View("API-centric selection test", Perspective.API_CENTRIC);
		view.addAPISelection(ScopedSelection.forScope(Scope.TYPE_SCOPE,  new QName("java.lang.Integer")));
		view.addAPISelection(ScopedSelection.forScope(Scope.METHOD_SCOPE, new QName("java.util.Iterator.hasNext()")));
		view.addAPISelection(ScopedSelection.forScope(Scope.PREFIX_SCOPE, new QName("javax")));
		view.addAPISelection(ScopedSelection.forScope(Scope.PREFIX_SCOPE, new QName("org.apache.commons")));
		view.addAPISelection(ScopedSelection.forScope(Scope.PACKAGE_SCOPE,new QName("org.apache.tools.ant")));
		view.addProjectSelection(UniversalSelection.getCurrent());
		view.setRenderable(false);

		return view;
	}

	public View testAPICentricSelectionView2() {
		View view = testAPICentricSelectionView();
		view.setName("API-centric selection test 2");
		view.removeProjectSelection(UniversalSelection.getCurrent());
		view.addProjectSelection(ScopedSelection.forScope(Scope.PREFIX_SCOPE, new QName("org.sunflow")));
		view.addProjectSelection(ScopedSelection.forScope(Scope.ROOT_SCOPE, new QName("tomcat")));		
		return view;
	}

	public View testProjectCentricSelectionView() {
		View view = new View("Project-centric selection test", Perspective.PROJECT_CENTRIC);
		view.addProjectSelection(ScopedSelection.forScope(Scope.ROOT_SCOPE, new QName(Store.Settings.PROJECT_TEST.getValue())));
		view.addAPISelection(ScopedSelection.forScope(Scope.TYPE_SCOPE, new QName("java.lang.String")));
		view.setRenderable(false);
		return view;
	}
	
	
	private static String TAGGED_API_VIEW_NAME = "Tagged APIs";
			
	public View testAPITagSelectionView() {
		View view = new View("API tag selection test", Perspective.API_CENTRIC);
		view.addProjectSelection(UniversalSelection.getCurrent());
		view.addAPISelection(ScopedSelection.forScope(Scope.TAG_SCOPE, new QName("ant")));
		view.addAPISelection(ScopedSelection.forScope(Scope.TAG_SCOPE, new QName("annotation"), new Tag("additional")));
		view.setAPISourceViewName(TAGGED_API_VIEW_NAME);
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
				ScopedSelection selection = null;
				QName name = new QName(prefix);
				//from apis.csv README: prune bit: 0 do not include (i.e., select?) subs, 1 do include subs
				if(subsincluded == 0) 
					selection = ScopedSelection.forScope(Scope.PACKAGE_SCOPE, name);
				else if(subsincluded == 1)
					selection = ScopedSelection.forScope(Scope.PREFIX_SCOPE, name);
				else throw new IOException("Fourth column should be either 0 or 1: " + line);
				if(!tag.isEmpty())
					selection.setTag(new Tag(tag));
				builder.add(selection);
				return true;
			}
			
			@Override
			public ImmutableList<Selection> getResult() {
				return builder.build();
			}
		});
		
		View view = new View(TAGGED_API_VIEW_NAME, Perspective.API_CENTRIC);
		for(Selection selection : selections) {
			view.addAPISelection(selection);
		}
		view.addProjectSelection(UniversalSelection.getCurrent());
		return view;

	}




}
