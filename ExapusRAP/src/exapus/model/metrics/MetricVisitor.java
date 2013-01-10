package exapus.model.metrics;

import exapus.model.view.View;
import exapus.model.visitors.IForestVisitor;

public abstract class MetricVisitor implements IForestVisitor {
    protected View view;

    protected MetricVisitor(View view) {
        this.view = view;
    }

}
