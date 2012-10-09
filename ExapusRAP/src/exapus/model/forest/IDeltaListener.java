package exapus.model.forest;

public interface IDeltaListener {
	public void add(DeltaEvent event);

	public void remove(DeltaEvent event);
}
