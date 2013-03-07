package exapus.model.tags;

import exapus.model.forest.*;
import exapus.model.view.View;
import exapus.model.visitors.IForestVisitor;

public class TagsPropagationVisitor implements IForestVisitor {

    private View view;

    public TagsPropagationVisitor(View view) {
        this.view = view;
    }

    @Override
    public boolean visitInboundFactForest(InboundFactForest forest) {
        return view.isAPICentric();
    }

    @Override
    public boolean visitOutboundFactForest(OutboundFactForest forest) {
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
        if (view.isAPICentric()) {
            inboundRef.getParent().addTagToAll(inboundRef.getTags());
        }
        return view.isAPICentric();
    }

    @Override
    public boolean visitOutboundReference(OutboundRef outboundRef) {
        if (view.isProjectCentric()) {
            outboundRef.getParent().addDualTagToAll(outboundRef.getDualTags());
        }
        return view.isProjectCentric();
    }
}
