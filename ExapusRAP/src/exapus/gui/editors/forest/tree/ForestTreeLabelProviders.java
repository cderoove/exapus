package exapus.gui.editors.forest.tree;

import exapus.model.forest.*;
import exapus.model.metrics.IMetricValue;
import exapus.model.metrics.MetricType;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ForestTreeLabelProviders {

	private static final Image PKTREE_IMG = AbstractUIPlugin.imageDescriptorFromPlugin("Exapus", "icons/packagefolder_obj.gif").createImage();
	private static final Image PKLAYER_IMG = AbstractUIPlugin.imageDescriptorFromPlugin("Exapus", "icons/package_obj.gif").createImage();
	private static final Image CLASS_IMG = AbstractUIPlugin.imageDescriptorFromPlugin("Exapus", "icons/class_obj.gif").createImage();
	private static final Image ENUM_IMG = AbstractUIPlugin.imageDescriptorFromPlugin("Exapus", "icons/enum_obj.gif").createImage();
	private static final Image INTERFACE_IMG = AbstractUIPlugin.imageDescriptorFromPlugin("Exapus", "icons/int_obj.gif").createImage();
	private static final Image METHOD_IMG = AbstractUIPlugin.imageDescriptorFromPlugin("Exapus", "icons/methpub_obj.gif").createImage();
	private static final Image FIELD_IMG = AbstractUIPlugin.imageDescriptorFromPlugin("Exapus", "icons/field_public_obj.gif").createImage();
	private static final Image ANNOTATION_IMG = AbstractUIPlugin.imageDescriptorFromPlugin("Exapus", "icons/annotation_obj.gif").createImage();

	public static class PatternColumnLabelProvider extends ColumnLabelProvider {

		// TODO: use foreground/background colors for metrics

        private boolean groupedPackageNames = false;

        public PatternColumnLabelProvider(boolean groupedPackageNames) {
            this.groupedPackageNames = groupedPackageNames;
        }

        public Image getImage(Object element) {
			if (element instanceof PackageTree)
				return PKTREE_IMG;
			if (element instanceof PackageLayer)
				return PKLAYER_IMG;
			if (element instanceof Member) {
				Element kind = ((Member) element).getElement();
				if (kind == Element.CLASS || kind == Element.ANONYMOUS_CLASS)
					return CLASS_IMG;
				if (kind == Element.INTERFACE)
					return INTERFACE_IMG;
				if (kind == Element.ENUM || kind == Element.ENUM_CONSTANT)
					return ENUM_IMG;
				if (kind == Element.INSTANCE_METHOD || kind == Element.STATIC_METHOD || kind == Element.CONSTRUCTOR)
					return METHOD_IMG;
				if (kind == Element.INSTANCE_FIELD || kind == Element.STATIC_FIELD)
					return FIELD_IMG;
				if (kind == Element.ANNOTATION_TYPEDECLARATION)
					return ANNOTATION_IMG;
			}
			return null;
		}

		// TODO: use HTML markup to display metrics
		public String getText(Object element) {
			if (element instanceof Ref) {
				Ref out = ((Ref) element);
				return out.getReferencingPattern().toString();
			}

			if (element instanceof PackageTree) {
				return ((PackageTree) element).getName().toString();
			}

			if (element instanceof PackageLayer) {
                if (groupedPackageNames) {

                    PackageLayer layer = (PackageLayer) element;
                    PackageTree packageTree = layer.getParentPackageTree();
                    if (packageTree != null && !"<Packages>".equals(packageTree.getName().toString())) {
                        return packageTree.getName().toString() + ":" + layer.getQName().toString();
                    }
                    return layer.getQName().toString();
                }
				return ((PackageLayer) element).getName().toString();
			}

			if (element instanceof Member) {
				return ((Member) element).getName().toString();
			}

			return element.toString();
		}
	}

	public static class ElementColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {
			if (element instanceof OutboundRef) {
				OutboundRef out = ((OutboundRef) element);
				return out.getReferencedElement().toString();
			}
			if (element instanceof InboundRef) {
				InboundRef in = ((InboundRef) element);
				return in.getReferencingElement().toString();
			}
			return null;
		}
	}

	public static class NameColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {
			if (element instanceof OutboundRef) {
				OutboundRef out = ((OutboundRef) element);
				return out.getReferencedName().toString();
			}
			if (element instanceof InboundRef) {
				InboundRef in = ((InboundRef) element);

                PackageTree packageTree = in.getDual().getParentPackageTree();
                if (packageTree != null && !"<Packages>".equals(packageTree.getName().toString())) {
                    return packageTree.getName().toString() + ":" + in.getReferencingName().toString();
                }

                return in.getReferencingName().toString();
			}
			return null;
		}

	}

	public static class LineColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {
			if (element instanceof Ref) {
				Ref r = ((Ref) element);
				return Integer.toString(r.getLineNumber());
			}
			return null;
		}

	}

    public static class MetricColumnLabelProvider extends ColumnLabelProvider {

        private boolean groupedPackageNames = false;
        private MetricType metricType;

        public MetricColumnLabelProvider(boolean groupedPackageNames, MetricType metricType) {
            this.groupedPackageNames = groupedPackageNames;
            this.metricType = metricType;
        }

        public String getText(Object element) {
            if (element instanceof ForestElement) {
                ForestElement fe = (ForestElement) element;
                IMetricValue metricValue = fe.getMetric(metricType);
                if (metricValue != null) return Integer.toString(metricValue.getValue(groupedPackageNames));
                else return null;
            }

            return null;
        }

    }

    public static class EmptyColumnLabelProvider extends ColumnLabelProvider {

        public String getText(Object element) {
            return "";
        }

    }

    // For debugging purposes
    public static class DebugColumnLabelProvider extends ColumnLabelProvider {

        public String getText(Object element) {
            return element.getClass().getCanonicalName();
        }

    }

}