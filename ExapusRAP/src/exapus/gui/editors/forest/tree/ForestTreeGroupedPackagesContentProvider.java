package exapus.gui.editors.forest.tree;

import com.google.common.collect.Iterables;
import exapus.model.forest.*;

import java.util.ArrayList;
import java.util.List;

public class ForestTreeGroupedPackagesContentProvider extends ForestTreeContentProvider {

    @Override
    public Object[] getElements(Object inputElement) {
        // build roots of tree
        if (inputElement instanceof FactForest) {
            List<MemberContainer> packages = new ArrayList<MemberContainer>();

            for (PackageTree packageTree : ((FactForest) inputElement).getPackageTrees()) {
                for (PackageLayer packageLayer : packageTree.getPackageLayers()) {
                    addToResult(packages, packageLayer);
                }
            }

            return Iterables.toArray(packages, MemberContainer.class);
        } else
            return null;
    }

    private void addToResult(List<MemberContainer> packages, PackageLayer layer) {
        if (layer.hasMembers()) {
            packages.add(layer);
        }

        for (PackageLayer nextLayer : layer.getPackageLayers()) {
            addToResult(packages, nextLayer);
        }
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof PackageLayer) {
            PackageLayer layer = (PackageLayer) parentElement;
            return Iterables.toArray(layer.getMembers(), MemberContainer.class);
        }
        if (parentElement instanceof Member) {
            Member member = (Member) parentElement;
            Iterable<Member> members = member.getMembers();
            Iterable<Ref> references = member.getReferences();
            return Iterables.toArray(Iterables.concat(members, references), Object.class);
        }

        return null;
    }
}
