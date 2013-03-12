package exapus.model.stats;

import exapus.model.forest.ForestElement;
import exapus.model.forest.Member;
import exapus.model.forest.PackageLayer;

public enum StatsLevel {
    // TODO method level
    GROUPED_PACKAGES, TOP_LEVEL_TYPES, METHODS, NONE;

    public static StatsLevel fromForestElement(ForestElement fe) {
        //fe instanceof PackageLayer ? StatsLevel.GROUPED_PACKAGES : StatsLevel.TOP_LEVEL_TYPES

        if (fe instanceof PackageLayer) return GROUPED_PACKAGES;
        if (fe instanceof Member && ((Member) fe).isTopLevel()) return TOP_LEVEL_TYPES;
        if (fe instanceof Member && ((Member) fe).getElement().isMethod()) return METHODS;

        return NONE;
    }
}
