package exapus.model.tags;

import exapus.model.forest.*;
import exapus.model.view.View;
import exapus.model.visitors.IForestVisitor;

public class TagsPropagationVisitor implements IForestVisitor {

    private View view;

    private FactForest forest;
    
    public TagsPropagationVisitor(View view) {
        this.view = view;
    }

    @Override
    public boolean visitInboundFactForest(InboundFactForest forest) {
    	this.forest = forest;
        return view.isAPICentric();
    }

    @Override
    public boolean visitOutboundFactForest(OutboundFactForest forest) {
    	this.forest = forest;
        return view.isProjectCentric();
    }

    @Override
    public boolean visitPackageTree(PackageTree packageTree) {
        return true;
    }

    @Override
    public boolean visitPackageLayer(PackageLayer packageLayer) {
        return true;
    }

    @Override
    public boolean visitMember(Member member) {
        return true;
    }

    @Override
    public boolean visitInboundReference(InboundRef inboundRef) {
    	inboundRef.getParent().addTagToAll(forest.getTagsFor(inboundRef));
    	return false;
    }

    @Override
    public boolean visitOutboundReference(OutboundRef outboundRef) {
    	outboundRef.getParent().addDualTagToAll(forest.getDualTagsFor(outboundRef));
    	return false;
    }
}
