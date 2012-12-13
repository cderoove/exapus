package exapus.model.view;

public class UniversalSelection extends Selection {
	
	private static UniversalSelection current = new UniversalSelection();
	
	public static UniversalSelection getCurrent() {
		return current;
	}
	
	private UniversalSelection() {
	}

}
