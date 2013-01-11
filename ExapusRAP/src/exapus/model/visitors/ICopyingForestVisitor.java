package exapus.model.visitors;

import exapus.model.forest.FactForest;

public interface ICopyingForestVisitor extends IForestVisitor {

	public FactForest copy(FactForest f);

}
