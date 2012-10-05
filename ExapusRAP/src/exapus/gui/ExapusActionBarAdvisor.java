package exapus.gui;

import java.net.URL;
import java.util.Dictionary;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;

import org.eclipse.rap.demo.editor.FooEditorInput;
/*
 import org.eclipse.rap.demo.wizard.SurveyWizard;
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.*;

import exapus.gui.actions.PopulateExapusModelAction;
import exapus.model.ExapusModel;

public class ExapusActionBarAdvisor extends ActionBarAdvisor {

	private Action aboutAction;
	private Action newEditorAction;
	private Action populateModelAction;

	public ExapusActionBarAdvisor(final IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(final IWorkbenchWindow window) {
		/*
		 * exitAction = ActionFactory.QUIT.create( window );
		 * exitAction.setImageDescriptor( quitActionImage ); register(
		 * exitAction );
		 * 
		 * importAction = ActionFactory.IMPORT.create( window ); register(
		 * importAction );
		 * 
		 * exportAction = ActionFactory.EXPORT.create( window ); register(
		 * exportAction );
		 * 
		 * saveAction = ActionFactory.SAVE.create( window ); register(
		 * saveAction );
		 * 
		 * saveAllAction = ActionFactory.SAVE_ALL.create( window ); register(
		 * saveAllAction );
		 * 
		 * preferencesAction = ActionFactory.PREFERENCES.create( window );
		 * register( preferencesAction );
		 */

		populateModelAction = new PopulateExapusModelAction();
		register(populateModelAction);

		newEditorAction = new Action() {

			public void run() {
				try {
					window.getActivePage().openEditor(new FooEditorInput(ExapusActionBarAdvisor.this), "org.eclipse.rap.demo.editor", true);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		};
		newEditorAction.setText("Open new editor");
		newEditorAction.setId("org.eclipse.rap.demo.neweditor");
		/*
		 * newEditorAction.setImageDescriptor( window.getWorkbench()
		 * .getSharedImages() .getImageDescriptor(
		 * ISharedImages.IMG_TOOL_NEW_WIZARD ) );
		 */
		register(newEditorAction);

		/*
		 * aboutAction = new Action() { public void run() { Shell shell =
		 * window.getShell(); Bundle bundle =
		 * Platform.getBundle(PlatformUI.PLUGIN_ID); Dictionary headers =
		 * bundle.getHeaders(); Object version =
		 * headers.get(Constants.BUNDLE_VERSION);
		 * MessageDialog.openInformation(shell, "RAP Workbench Demo",
		 * "Running on RAP version " + version); } };
		 * aboutAction.setText("About");
		 * aboutAction.setId("org.eclipse.rap.demo.about"); //
		 * aboutAction.setImageDescriptor( helpActionImage );
		 * register(aboutAction);
		 * 
		 * /* showViewMenuMgr = new MenuManager( "Show View", "showView" );
		 * IContributionItem showViewMenu =
		 * ContributionItemFactory.VIEWS_SHORTLIST.create( window );
		 * showViewMenuMgr.add( showViewMenu );
		 */

		/*
		 * wizardAction = new Action() { public void run() { SurveyWizard wizard
		 * = new SurveyWizard(); WizardDialog dlg = new WizardDialog(
		 * window.getShell(), wizard ); dlg.open(); } }; wizardAction.setText(
		 * "Open wizard" ); wizardAction.setId( "org.eclipse.rap.demo.wizard" );
		 * wizardAction.setImageDescriptor( wizardActionImage ); register(
		 * wizardAction );
		 */

		/*
		 * browserAction = new Action() { public void run() { browserIndex++;
		 * try { window.getActivePage() .showView(
		 * "org.eclipse.rap.demo.DemoBrowserViewPart", String.valueOf(
		 * browserIndex ), IWorkbenchPage.VIEW_ACTIVATE ); } catch(
		 * PartInitException e ) { e.printStackTrace(); } } };
		 * browserAction.setText( "Open new Browser View" );
		 * browserAction.setId( "org.eclipse.rap.demo.browser" );
		 * browserAction.setImageDescriptor( browserActionImage ); register(
		 * browserAction );
		 */

	}

	protected void fillMenuBar(final IMenuManager menuBar) {

		/*
		 * MenuManager fileMenu = new MenuManager( "File",
		 * IWorkbenchActionConstants.M_FILE ); MenuManager windowMenu = new
		 * MenuManager( "Window", IWorkbenchActionConstants.M_WINDOW );
		 * MenuManager helpMenu = new MenuManager( "Help",
		 * IWorkbenchActionConstants.M_HELP );
		 * 
		 * menuBar.add( fileMenu );
		 * 
		 * fileMenu.add( importAction ); fileMenu.add( exportAction );
		 * fileMenu.add( exitAction );
		 * 
		 * 
		 * 
		 * windowMenu.add( showViewMenuMgr ); windowMenu.add( preferencesAction
		 * );
		 * 
		 * menuBar.add( windowMenu ); menuBar.add( helpMenu );
		 * 
		 * helpMenu.add( rapWebSiteAction ); helpMenu.add( new Separator(
		 * "about" ) ); helpMenu.add( aboutAction );
		 */
	}

	protected void fillCoolBar(final ICoolBarManager coolBar) {
		createWorkbenchToolBar(coolBar);
		createEditorToolBar(coolBar);
	}

	private void createWorkbenchToolBar(final ICoolBarManager coolBar) {
		IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		coolBar.add(new ToolBarContributionItem(toolbar, "workbench"));
		toolbar.add(populateModelAction);
	}

	private void createEditorToolBar(final ICoolBarManager coolBar) {
		IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		coolBar.add(new ToolBarContributionItem(toolbar, "editor"));
		toolbar.add(newEditorAction);
	}

	protected void fillStatusLine(final IStatusLineManager statusLine) {
		// statusLine.add( aboutAction );
	}
}
