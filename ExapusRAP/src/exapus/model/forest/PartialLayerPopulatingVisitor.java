package exapus.model.forest;

import java.util.Set;

import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class PartialLayerPopulatingVisitor extends LayerPopulatingVisitor {

	private Set<String> sourcePackageNames;
	
	public PartialLayerPopulatingVisitor(PackageLayer l, Set<String> sourcePackageNames) {
		super(l);
		this.sourcePackageNames = sourcePackageNames;
	}
	
	@Override
	public boolean isTypeBindingFromAPI(ITypeBinding b) {
		if (b.isPrimitive())
			return false;
		if (b.isArray()) {
			ITypeBinding elementType = b.getElementType();
			return isTypeBindingFromAPI(elementType);
		}
		
		IPackageBinding ip = b.getPackage();
		String name = ip.getName();
		
		if(sourcePackageNames.contains(name))
			return false;
		return true;
	}

	
	
	
	
	

}
