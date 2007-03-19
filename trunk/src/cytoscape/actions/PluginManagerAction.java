/**
 * 
 */
package cytoscape.actions;

import cytoscape.plugin.PluginManager;
import cytoscape.plugin.PluginInfo;
import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.dialogs.PluginInstallDialog;
import cytoscape.dialogs.PluginListDialog;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;

import javax.swing.JOptionPane;


/**
 * @author skillcoy
 *
 */
public class PluginManagerAction extends CytoscapeAction
	{

	/**
	 * 
	 */
	public PluginManagerAction()
		{
		super("Manage Plugins");
		setPreferredMenu("Plugins");
		}


	/* (non-Javadoc)
	 * @see cytoscape.util.CytoscapeAction#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e)
		{
		/*
		 * This will actually pop up the "currently installed" dialog box which will 
		 * have a button to "install plugins" poping up the PluginInstallDialog
		 */
		
		PluginManager Mgr = CytoscapeInit.getPluginManager();
		PluginInfo[] Info = Mgr.getInstalledPlugins();
		
		PluginListDialog dialog = new PluginListDialog();
		dialog.createTable(Info);
		dialog.pack();
		dialog.setVisible(true);
		}

	}
