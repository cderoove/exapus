package exapus.gui.editors.forest.graph;

import exapus.model.forest.ForestElement;

public class Node implements INode {

    public static enum SpecialCase {
        TOP_LEVEL_TAG_WITH_PREFIX
    }

    private ForestElement fe;
    private SpecialCase spCase;

    public Node() {
    }

    public Node(ForestElement fe) {
        this.fe = fe;
    }

    public Node(ForestElement fe, SpecialCase spCase) {
        this.fe = fe;
        this.spCase = spCase;
    }

    public ForestElement getFe() {
        return fe;
    }

    public void setFe(ForestElement fe) {
        this.fe = fe;
    }

    public SpecialCase getSpCase() {
        return spCase;
    }

    public void setSpCase(SpecialCase spCase) {
        this.spCase = spCase;
    }

    @Override
    public String toString() {
        return String.format("fe=%s, spCase=%s", fe.getQName().toString(), spCase == null ? "" : spCase.name());
    }
}
