/*
  File: PluginsTableModel.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
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
package cytoscape.dialogs.preferences;

import cytoscape.*;
import cytoscape.logger.CyLogger;

import java.io.*;

import java.net.URL;

import java.util.*;

import javax.swing.*;
import javax.swing.table.*;


/**
 * @deprecated This class is no longer used in cytoscape.  If you are using it,
 * let us know and we'll figure something out.  Otherwise, this will be
 * removed December 2007.
 */
public class PluginsTableModel extends AbstractTableModel {
	static int[] columnWidth = new int[] { 400 };
	static int[] alignment = new int[] { JLabel.LEFT };
	private Properties properties;
	TreeSet pluginsSet = new TreeSet();
	static String[] columnHeader = new String[] { "Plugin Location" };
	boolean pluginsFromCommandLineLoadedAndSaved;
	private static CyLogger logger = CyLogger.getLogger(PluginsTableModel.class);

	/**
	 * Creates a new PluginsTableModel object.
	 */
	public PluginsTableModel() {
		super();
		pluginsFromCommandLineLoadedAndSaved = false;
		// get only one entry from properties: key=plugins
		properties = new Properties();

		if (CytoscapeInit.getProperties().get("plugins") != null) {
			properties.put("plugins", CytoscapeInit.getProperties().get("plugins"));
		}

		loadProperties();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void loadProperties() {
		pluginsSet.clear();

		if (getProperty("plugins") != null) {
			String[] pargs = getProperty("plugins", "").split(",");

			for (int i = 0; i < pargs.length; i++) {
				addPlugin(pargs[i]);
			}
		}

		// now (non-redundantly) include plugins specified on the command line
		if (!pluginsFromCommandLineLoadedAndSaved) {
			Set plugins = new HashSet(cytoscape.plugin.PluginManager.getPluginURLs());
			Iterator iterator = plugins.iterator();

			while (iterator.hasNext()) {
				URL url = (URL) iterator.next();
				addPlugin(url);
			}
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param key DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param key DOCUMENT ME!
	 * @param defaultValue DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param key DOCUMENT ME!
	 * @param value DOCUMENT ME!
	 */
	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}

	// Add plugin to plugins=... String in private Properties object for
	/**
	 *  DOCUMENT ME!
	 *
	 * @param newPlugin DOCUMENT ME!
	 */
	public void addPluginToPropertyString(String newPlugin) {
		String tempPlugins = properties.getProperty("plugins");

		if (tempPlugins != null) {
			//cull out duplicate entries, since single entries and directory adds
			// can result in duplicates not specified strictly at UI level
			properties.setProperty("plugins", cullDuplicates(tempPlugins + "," + newPlugin));
		} else {
			properties.setProperty("plugins", newPlugin);
		}
	}

	/*
	 * cull duplicate entries in comma-separated tokens in String
	 */
	String cullDuplicates(String s) {
		StringTokenizer st = new StringTokenizer(s, ",");
		HashSet hashSet = new HashSet();

		while (st.hasMoreTokens()) {
			hashSet.add(st.nextToken());
		}

		String newString = new String();
		Iterator it = hashSet.iterator();
		StringBuffer sb = new StringBuffer();

		while (it.hasNext()) {
			sb.append((String) it.next());
			sb.append(",");
		}

		sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param deletedPlugins DOCUMENT ME!
	 */
	public void deletePlugins(String[] deletedPlugins) {
		// deletion a little weird - multiple selection deletes not working
		// right - restricting via setting Tables to use single selection

		// remove deleted plugin from plugins=... property
		String tempPlugins = properties.getProperty("plugins");

		for (int k = 0; k < deletedPlugins.length; k++) {
			String value = deletedPlugins[k];

			if (value.startsWith("file:"))
				value = value.substring(5);
			else if (value.startsWith("jar:"))
				value = value.substring(4);
			else if (value.startsWith("http\\:")) // don't want the backslash
				value = value.substring(0, 3) + value.substring(5);

			// NB: \\ is escaped "\"
			if (tempPlugins != null) {
				String[] plugins = tempPlugins.split(",");
				String returnString = null;

				for (int i = 0; i < plugins.length; i++) {
					if (value.compareTo(plugins[i]) == 0) {
					} else {
						if (returnString == null) {
							returnString = new String(plugins[i]);
						} else {
							returnString = new String(returnString + "," + plugins[i]);
						}
					}
				}

				if (returnString == null) {
					properties.remove("plugins");
				} else {
					properties.setProperty("plugins", returnString);
				}
			}

			// and remove deleted plugins from TreeSet for model and table
			pluginsSet.remove(value);
		}
	}

	// add plugin to table and properties object
	/**
	 *  DOCUMENT ME!
	 *
	 * @param pluginString DOCUMENT ME!
	 */
	public void addPlugin(String pluginString) {
		String[] plugin = pluginString.split(",");

		for (int i = 0; i < plugin.length; i++) {
			URL url;

			try {
				if (plugin[i].startsWith("http")) {
					plugin[i] = "jar:" + plugin[i] + "!/";
					url = new URL(plugin[i]);
				} else if (plugin[i].startsWith("file") || plugin[i].startsWith("jar")) {
					// do no massaging of string, just create URL
					url = new URL(plugin[i]);
				} else {
					url = new URL("file", "", plugin[i]);
				}

				addPlugin(url);
			} catch (Exception ue) {
				logger.warn("Error: cannot construct URL from: " + plugin[i]);
			}
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param u DOCUMENT ME!
	 */
	public void addPlugin(URL u) {
		if (u != null) {
			// get string info (no protocol) for insertion into table
			// and plugins=... property
			String path = u.getPath();

			// strip off trailing "!/" for JAR URLs
			if (path.endsWith("!/"))
				path = path.substring(0, path.length() - 2);

			pluginsSet.add(path); // add to TreeSet for model and table
			addPluginToPropertyString(path); // also add to plugins=... string
			                                 //   in private properties object
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param col DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getColumnName(int col) {
		return columnHeader[col];
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param saveToProps DOCUMENT ME!
	 */
	public void save(Properties saveToProps) {
		// save local property values to passed-in Properties
		saveToProps.putAll(properties);
		// mark these plugins loaded from the command line as loaded
		// and saved, so no need to reparse, etc.
		pluginsFromCommandLineLoadedAndSaved = true;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param restoreFromProps DOCUMENT ME!
	 */
	public void restore(Properties restoreFromProps) {
		properties.clear();

		if (restoreFromProps.getProperty("plugins") != null) {
			properties.put("plugins", restoreFromProps.getProperty("plugins"));
			loadProperties(); // now get pluginsSet populated from properties
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getColumnCount() {
		return columnHeader.length;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param row DOCUMENT ME!
	 * @param col DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getValueAt(int row, int col) {
		Object retVal = new String("");
		int index = 0;

		for (Iterator it = pluginsSet.iterator(); it.hasNext();) {
			retVal = it.next();

			if (index == row)
				break;

			index++;
		}

		return retVal;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getRowCount() {
		return pluginsSet.size();
	}

	/*
	 * check for presence of name/value pair in table/table model
	 */

	/**
	 *  DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 * @param listOfDuplicates DOCUMENT ME!
	 * @param listOfNew DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean validateNewPlugins(String value, Vector listOfDuplicates, Vector listOfNew) {
		listOfDuplicates.clear();
		listOfNew.clear();

		try {
			// handle comma separated list of paths, or single path
			String[] path = value.split(",");

			for (int i = 0; i < path.length; i++) {
				File newFile = new File(path[i]);
				int numRows = getRowCount();
				boolean isDuplicate = false;

				for (int j = 0; j < numRows; j++) {
					String currentFileStr = (String) getValueAt(j, 0);

					// test for simple string match - path[i] already in list
					if (currentFileStr.equals(path[i])) {
						listOfDuplicates.add(path[i]);
						isDuplicate = true;
					} else {
						// not textually in list, but must test for File equivalence
						// - are the two file paths the same file?
						File currentFile = new File(currentFileStr);

						if ((currentFile.getCanonicalPath()).equals(newFile.getCanonicalPath())) {
							listOfDuplicates.add(path[i]);
							isDuplicate = true;
						}
					}
				}

				if (!isDuplicate) {
					listOfNew.add(path[i]);
				}
			}
		} catch (IOException ioe) {
			logger.warn("Failed to validate new plugins: "+ioe.getMessage(), ioe);
		}

		return ((listOfDuplicates.size() > 0) ? true : false);
	}
}
