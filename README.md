Exapus
======

Exapus is a web application for exploring the usage of APIs within a single project (i.e., project-centric exploration) and across a corpus of projects (i.e., api-centric exploration) along the dimensions of where, how much and in what manner. Several metrics and visualizations are provided. 


## Installation

Exapus has been tested against Eclipse Juno. 

* Import the `ExapusRAP` folder from the repository into your Eclipse workspace as the pre-configured `Exapus` project.  
For instance, using the pre-installed [EGit](http://www.eclipse.org/egit/) ([tutorial](http://www.vogella.com/articles/EGit/article.html) plugin for Eclipse:
    * Choose `Import…` from the `File` menu.
    * Select `Git` > `Projects from Git`
    * Choose `URI` and enter the details of this repository (authentication is needed for write access).
    * Select the `master` branch.
    * If necessary, change the destination directory for your local repository clone. 
    * Choose `Import existing projects` as the wizard to be used for the import. Leave the 'Working Directory` root selected.
    * Select the project named `Exapus` which corresponds to the folder `ExapusRap` folder and finish. Note that the project will not yet build correctly.
 

* Configure the project's [target platform](http://www.vogella.com/articles/EclipseTargetPlatform/article.html).
  * Open the `ExapusRAP.target` file in the root of the project by double clicking. 
  * Click on `Set as Target Platform` in the top-right corner of the editor. The project should now have been built without errors.


* Configure the project's launcher.
  * Choose `Run Configurations…` from the `Run` menu. 
  * Select (but don't double click) the `RAP Application` > `ExapusRAP` configuration. 
  * Edit the `Location` field in the `Instance Area` pane of the `Main` tab such that it points to the path of the Eclipse workspace that should be analyzed by Exapus (e.g., by using the `File System…` button). Note that:
    * Exapus will analyze only the projects that are open in the workspace  
    * opened projects should build without errors
    * no Eclipse instance should be open on the selected workspace
   
## Launching Exapus

Once the project's launcher has been configured, the Exapus web application can be started by double clicking its launcher (see above) or by clicking the  green "play" toolbar button while the Exapus project is selected in workspace.

Your system's default browser should automatically open on the application's URL. In case of an `HTTP error 404`, refresh the URL. In all likelihood, the URL was accessed before the application finished launching. 

## License
Copyright © 2012-2013 Exapus contributors.
Distributed under the Eclipse Public License.

External dependencies:
* [Eclipse RAP](http://eclipse.org/rap/) for the widget toolkit 
* [Eclipse JDT](http://www.eclipse.org/jdt/) for the Java project analysis
