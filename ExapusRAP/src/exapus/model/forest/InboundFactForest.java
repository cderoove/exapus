package exapus.model.forest;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;

import exapus.model.visitors.IForestVisitor;

public class InboundFactForest extends FactForest {

	public static UqName DEFAULT_TREE_NAME = new UqName("<Selected APIs>");
	
	private PackageTree root;
	
	public InboundFactForest(ExapusModel m) {
		super(m, Direction.INBOUND);
		root = new PackageTree(DEFAULT_TREE_NAME);
		addPackageTree(root);
	}
	
	public void addInboundAPIReference(ITypeBinding b, OutboundRef outbound) {
		root.addInboundReference(b, outbound);
	}

	public void addInboundAPIReference(ITypeBinding tb, IVariableBinding fb, OutboundRef outbound) {
		root.addInboundReference(tb, fb, outbound);
	}

	public void addInboundAPIReference(ITypeBinding tb, IMethodBinding mb, OutboundRef outbound) {
		root.addInboundReference(tb, mb, outbound);
	}

	public FactForest getDualFactForest() {
		return getModel().getProjectCentricForest();
	}
	
	public void acceptVisitor(IForestVisitor v) {
		if(v.visitInboundFactForest(this)) 
			for(PackageTree t : getPackageTrees()) 
				t.acceptVisitor(v);
	}

	

}
