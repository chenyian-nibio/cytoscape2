// JarLoaderUI
//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.jarLoader;
//--------------------------------------------------------------------------
import cytoscape.*;
import javax.swing.*;

//--------------------------------------------------------------------------
public class JarLoaderUI {
    protected CytoscapeWindow cytoscapeWindow;
    public JarLoaderUI (CytoscapeWindow cytoscapeWindow, JMenu theMenu)
    {
	this.cytoscapeWindow = cytoscapeWindow;
	//cytoscapeWindow.getOperationsMenu().add
	theMenu.add
	    (new JarPluginLoaderAction (cytoscapeWindow));
	//cytoscapeWindow.getOperationsMenu().add
	theMenu.add
	    (new JarPluginDirectoryAction (cytoscapeWindow));
	String[] args = cytoscapeWindow.getConfiguration().getArgs();
	JarLoaderCommandLineParser parser =
	    new JarLoaderCommandLineParser(args,cytoscapeWindow);
    }
} // class JarLoaderUI


