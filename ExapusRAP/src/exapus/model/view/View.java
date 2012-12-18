package exapus.model.view;

import java.util.ArrayList;
import java.util.List;

public abstract class View {

	public View(String n, Perspective p) {
		name = n;
		perspective = p;
		apiselection = new ArrayList<Selection>();
		projectselection = new ArrayList<Selection>();
	}
	
	private static 	Perspective[] supportedPerspectives = {Perspective.API_CENTRIC, Perspective.PROJECT_CENTRIC};

	public static Perspective[] supportedPerspectives() {
		return supportedPerspectives;
	}

	private Perspective perspective;
	
	private List<Selection> apiselection;
	
	private List<Selection> projectselection;
	
	private String name;

	public Perspective getPerspective() {
		return perspective;
	}

	public void setPerspective(Perspective perspective) {
		this.perspective = perspective;
	}

	public Iterable<Selection> getAPISelections() {
		return apiselection;
	}

	public void addAPISelection(Selection selection) {
		apiselection.add(selection);
	}
	
	public boolean removeAPISelection(Selection selection) {
		return apiselection.remove(selection);
	}

	public Iterable<Selection> getProjectSelections() {
		return projectselection;
	}

	public void addProjectSelection(Selection selection) {
		projectselection.add(selection) ;
	}
	
	public boolean removeProjectSelection(Selection selection) {
		return projectselection.remove(selection);
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
	
	public abstract boolean isAPICentric();
	
	public abstract boolean isProjectCentric();
	
	
}
