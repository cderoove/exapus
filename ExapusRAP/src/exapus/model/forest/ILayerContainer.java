package exapus.model.forest;

public interface ILayerContainer {
	public void addLayer(PackageLayer l);
	public Iterable<PackageLayer> getPackageLayers();
	public Iterable<PackageLayer> getAllPackageLayers();
	public boolean removePackageLayer(PackageLayer l);
}
