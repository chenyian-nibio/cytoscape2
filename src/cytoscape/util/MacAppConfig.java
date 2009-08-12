/*
  File: MacAppConfig.java

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
package cytoscape.util;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import org.jdom.input.SAXBuilder;

import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Configures the Mac OS X Info.plist file associated with Cytoscape.
 * This enables us to run a "Macified" version of Cytoscape, with its
 * own icon, integrated menus, etc.  The plist file is part of the package
 * contents automatically generated by the Mac OS X Jar Bundler application.
 *
 * The existing Info.plist file checked into CVS provides preset
 * configuration settings.  This class modified the existing file by adding
 * all JAR files in cytoscape/lib to the plist file.  It does so be replacing
 * the JAR_ARRAY element with the correct array element, and then adding
 * all the correct JAR files.  By automating this task, we can easily
 * update the Mac OS X application as part of our regular build process.
 *
 * Full documentation on the Mac OS X Jar Bundler is available at:
 * http://developer.apple.com/documentation/Java/Conceptual/Jar_Bundler
 *
 * @author Ethan Cerami
 */
public class MacAppConfig {
	/**
	 * Name of Jar Bundler Configuration File.
	 */
	private String configFile = "Cytoscape.app/Contents/Info.plist";

	/**
	 * Configures the Info.plist list with all cytoscape/*.jar files.
	 * @throws IOException Error Reading Document.
	 * @throws JDOMException Error Processing XML Document.
	 */
	public void configure() throws IOException, JDOMException {
		// Parse Info.plist file
		Document doc = getConfiguration();

		//  Get all files in cytocape/lib
		String[] files = getJarList();

		//  Get the Correct JAR_ARRAY Element Placeholder.
		Element root = doc.getRootElement();
		Element dict = root.getChild("dict");
		dict = dict.getChild("dict");

		Element array = dict.getChild("JAR_ARRAY");
		array.setName("array");
		addJars(files, array);

		//  Overwrite existing Info.plist file.
		// jdom 0.9
		//XMLOutputter outputter = new XMLOutputter("     ", false);
		// jdom 1.0
		XMLOutputter outputter = new XMLOutputter(org.jdom.output.Format.getPrettyFormat());

		FileWriter writer = new FileWriter(configFile);
        try {
            outputter.output(doc, writer);
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
		CyLogger.getLogger().info("File is now updated with correct JARs:  " + configFile);
	}

	/**
	 * Add all JAR Files to Info.plist
	 * @param files Array of Files in cytocape/lib.
	 * @param array Array Element in Info.plist.
	 */
	private void addJars(String[] files, Element array) {
		for (int i = 0; i < files.length; i++) {
			String file = files[i];

			if (file.endsWith("jar")) {
				Element jar = new Element("string");
				jar.setText("$JAVAROOT/" + file);
				array.addContent(jar);
				array.addContent("\n");
			}
		}
	}

	/**
	 * Gets all Files in cytoscape/lib
	 * @return Array of File Strings.
	 */
	private String[] getJarList() {
		File dir = new File("lib");
		String[] files = dir.list();

		return files;
	}

	/**
	 * Parses Info.Plist file into JDOM Document object.
	 * @return JDOM Document Object.
	 * @throws IOException Error Reading Document.
	 * @throws JDOMException Error Processing XML Document.
	 */
	private Document getConfiguration() throws IOException, JDOMException {
		Document doc = null;

		try {
			FileReader reader = new FileReader(configFile);
            try {
                SAXBuilder saxBuilder = new SAXBuilder();
                doc = saxBuilder.build(reader);
            }
            finally {
                if (reader != null) {
                    reader.close();
                }
            }
		} catch (FileNotFoundException e) {
			CyLogger.getLogger().info("Cannot find:  " + configFile);
			CyLogger.getLogger().info("Try running:  'ant mac' first.");
			Cytoscape.exit(-1);
		}

		return doc;
	}

	/**
	 * Main Method.
	 * @param args Command Line Arguments.
	 * @throws Exception All Exceptions.
	 */
	public static void main(String[] args) throws Exception {
		MacAppConfig mac = new MacAppConfig();
		mac.configure();
	}
}
