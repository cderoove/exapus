package exapus.gui.util;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

//from http://www.eclipse.org/forums/index.php/m/657553/
public class FileDownloadDialog extends Dialog {
	private Browser b;
	private String url;

	public FileDownloadDialog(Shell parent) {
		super(parent);
	}

	public void setURL (String url) {
		if (b != null && !b.isDisposed()) {
			b.setUrl(url);
		}
		this.url = url;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Control control = super.createDialogArea(parent);
		b = new Browser(parent, SWT.NONE);
		if (url != null) {
			b.setUrl(url);
		}
		return control;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		return null;
	}

	@Override
	protected int getShellStyle() {
		return SWT.NO_TRIM;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setSize(1, 1);
		newShell.setMinimized(true);
	}
}

