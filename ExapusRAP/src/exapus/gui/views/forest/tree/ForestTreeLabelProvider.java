package exapus.gui.views.forest.tree;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import exapus.model.forest.Element;
import exapus.model.forest.Member;
import exapus.model.forest.OutboundRef;
import exapus.model.forest.PackageLayer;
import exapus.model.forest.PackageTree;

public class ForestTreeLabelProvider implements ITableLabelProvider {

	private static final Image PKTREE_IMG = AbstractUIPlugin.imageDescriptorFromPlugin("ExapusRAP", "/icons/packagefolder_obj.gif").createImage();
	private static final Image PKLAYER_IMG = AbstractUIPlugin.imageDescriptorFromPlugin("ExapusRAP", "/icons/package_obj.gif").createImage();
	private static final Image CLASS_IMG = AbstractUIPlugin.imageDescriptorFromPlugin("ExapusRAP", "/icons/class_obj.gif").createImage();
	private static final Image ENUM_IMG = AbstractUIPlugin.imageDescriptorFromPlugin("ExapusRAP", "/icons/enum_obj.gif").createImage();
	private static final Image INTERFACE_IMG = AbstractUIPlugin.imageDescriptorFromPlugin("ExapusRAP", "/icons/int_obj.gif").createImage();
	private static final Image METHOD_IMG = AbstractUIPlugin.imageDescriptorFromPlugin("ExapusRAP", "/icons/methpub_obj.gif").createImage();
	private static final Image FIELD_IMG = AbstractUIPlugin.imageDescriptorFromPlugin("ExapusRAP", "/icons/field_public_obj.gif").createImage();
	private static final Image ANNOTATION_IMG = AbstractUIPlugin.imageDescriptorFromPlugin("ExapusRAP", "/icons/annotation_obj.gif").createImage();

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
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

		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		/*
		 * 0: pattern 1: element 2: name
		 */

		if (element instanceof OutboundRef) {
			OutboundRef out = ((OutboundRef) element);
			switch (columnIndex) {
			case 0:
				return out.getReferencingPattern().toString();
			case 1:
				return out.getReferencedElement().toString();
			case 2:
				return out.getReferencedName().toString();
			}
		} else if (columnIndex == 0) {
			return element.toString();
		}
		return null;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

}
