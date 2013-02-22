package exapus.gui.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Util {
	
	//should correspond to the one in plugin.xml (usally put in plugin Activator, but there is none now)
	public static String PLUGIN_ID = "Exapus";

	public static void asyncUIThreadIfWidgetNotDisposed(Widget w, Runnable r) {
		if(w == null)
			return;
		if(w.isDisposed())
			return;
		Display display = w.getDisplay();
		display.asyncExec(r);
	}

	public static ImageDescriptor getImageDescriptorFromPlugin(String name) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "/icons/" + name);
	}
	
	public static Image getImageFromPlugin(String name) {
		ImageDescriptor descriptor = getImageDescriptorFromPlugin(name);
		if(descriptor == null)
			return null;
		return descriptor.createImage();
	}


	
	


}
