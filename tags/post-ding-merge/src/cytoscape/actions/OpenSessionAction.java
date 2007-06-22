
/*
  File: OpenSessionAction.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.readers.CytoscapeSessionReader;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

/**
 * Open session file. This class will load all networks and session state in the
 * cys file.
 * 
 * @author kono
 * 
 */
public class OpenSessionAction extends CytoscapeAction {

	// Extension for the new cytoscape session file
	public static String SESSION_EXT = "cys";

	public OpenSessionAction() {
		super("Open");
		setPreferredMenu("File");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_O, ActionEvent.CTRL_MASK);
	}

	public OpenSessionAction(boolean label) {
		super();
	}

	// If no current session file exists, open dialog box to save new session,
	// and if it exists, overwrite the file.
	public void actionPerformed(ActionEvent e) {

		String name; // file name to be opened.

		boolean proceed = prepare();

		if (proceed == true) {

			// Create FileFilters
			CyFileFilter sessionFilter = new CyFileFilter();

			// Add accepted File Extensions
			sessionFilter.addExtension(SESSION_EXT);
			sessionFilter.setDescription("Cytoscape Session files");

			// Open Dialog to ask user the file name.
			try {
				name = FileUtil.getFile("Open a Session File", FileUtil.LOAD,
						new CyFileFilter[] { sessionFilter }).toString();
			} catch (Exception exp) {
				// this is because the selection was canceled
				return;
			}

			// Close all networks in the workspace.
			cleanWorkspace();

			System.out.println("Opening session file: " + name);
			Cytoscape.setCurrentSessionFileName(name);

			// Create Task
			OpenSessionTask task = new OpenSessionTask(name);

			// Configure JTask Dialog Pop-Up Box
			JTaskConfig jTaskConfig = new JTaskConfig();
			jTaskConfig.setOwner(Cytoscape.getDesktop());
			jTaskConfig.displayCloseButton(true);
			jTaskConfig.displayCancelButton(true);
			jTaskConfig.displayStatus(true);
			jTaskConfig.setAutoDispose(false);

			// Execute Task in New Thread; pop open JTask Dialog Box.
			TaskManager.executeTask(task, jTaskConfig);

		}

	}

	/**
	 * Before loading the new session, we need to clean up current session.
	 * 
	 */
	private boolean prepare() {

		int currentNetworkCount = Cytoscape.getNetworkSet().size();

		if (currentNetworkCount != 0) {
			// Show warning
			String warning = "Current session will be lost.\nDo you want to continue?";

			int result = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(),
					warning, "Caution!", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null);

			if (result == JOptionPane.YES_OPTION) {

				return true;
			} else {
				return false;
			}

		} else {
			return true;
		}

	}

	// Clean-up current session
	// Destroy all networks
	private void cleanWorkspace() {

		Set netSet = Cytoscape.getNetworkSet();
		Iterator it = netSet.iterator();

		while (it.hasNext()) {
			CyNetwork net = (CyNetwork) it.next();
			Cytoscape.destroyNetwork(net);
		}

	}

} // SaveAsGMLAction

class OpenSessionTask implements Task {

	private String fileName;
	private TaskMonitor taskMonitor;

	// private CytoscapeSessionReader sr;

	/**
	 * Constructor.
	 * 
	 */
	OpenSessionTask(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Executes Task
	 * 
	 * @throws
	 * @throws Exception
	 */
	public void run() {
		taskMonitor.setStatus("Opening Session File...");
		taskMonitor.setPercentCompleted(-1);

		CytoscapeSessionReader sr = new CytoscapeSessionReader(fileName);

		try {
			sr.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block

			taskMonitor.setException(e, "Cannot open the session file.");

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Session file " + fileName
				+ " successfully loaded.");
		// Show the session Name as the window title.
		File shortName = new File(fileName);
		Cytoscape.getDesktop()
				.setTitle(
						"Cytoscape Desktop (Session Name: "
								+ shortName.getName() + ")");
	}

	/**
	 * Halts the Task: Not Currently Implemented.
	 */
	public void halt() {
		// Task can not currently be halted.
	}

	/**
	 * Sets the Task Monitor.
	 * 
	 * @param taskMonitor
	 *            TaskMonitor Object.
	 */
	public void setTaskMonitor(TaskMonitor taskMonitor)
			throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	/**
	 * Gets the Task Title.
	 * 
	 * @return Task Title.
	 */
	public String getTitle() {
		return new String("Opening Session File");
	}

}