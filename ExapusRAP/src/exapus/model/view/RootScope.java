package exapus.model.view;

public class RootScope extends Scope {
	
	private static RootScope current = new RootScope();
	
	public static RootScope getCurrent() {
		return current;
	}
	
	private RootScope() {
	}
	
	@Override
	public String toString() {
		return "rootScope";
	}	


}
