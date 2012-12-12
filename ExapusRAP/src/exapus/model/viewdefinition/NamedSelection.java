package exapus.model.viewdefinition;

import java.util.List;

import exapus.model.forest.UqName;

public class NamedSelection {
	
	public class ScopedName {
		public Scope scope;
		
		public UqName name;
	}

	public List<ScopedName> selected;
	
}
