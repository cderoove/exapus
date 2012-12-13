package exapus.gui.util;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

public class Util {

	public static void asyncUIThreadIfWidgetNotDisposed(Widget w, Runnable r) {
		if(w == null)
			return;
		if(w.isDisposed())
			return;
		Display display = w.getDisplay();
		display.asyncExec(r);
	}



}
