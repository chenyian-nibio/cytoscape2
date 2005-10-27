// ThesaurusFlatFileReader.java

/**
 * Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute * *
 * This library is free software; you can redistribute it and/or modify it *
 * under the terms of the GNU Lesser General Public License as published * by
 * the Free Software Foundation; either version 2.1 of the License, or * any
 * later version. * * This library is distributed in the hope that it will be
 * useful, but * WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF *
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. The software and *
 * documentation provided hereunder is on an "as is" basis, and the * Institute
 * for Systems Biology and the Whitehead Institute * have no obligations to
 * provide maintenance, support, * updates, enhancements or modifications. In no
 * event shall the * Institute for Systems Biology and the Whitehead Institute *
 * be liable to any party for direct, indirect, special, * incidental or
 * consequential damages, including lost profits, arising * out of the use of
 * this software and its documentation, even if the * Institute for Systems
 * Biology and the Whitehead Institute * have been advised of the possibility of
 * such damage. See * the GNU Lesser General Public License for more details. * *
 * You should have received a copy of the GNU Lesser General Public License *
 * along with this library; if not, write to the Free Software Foundation, *
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

// ------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
// ------------------------------------------------------------------------------
package cytoscape.data.synonyms.readers;

// ------------------------------------------------------------------------------
import java.io.*;
import java.util.Vector;
import java.util.List;
import java.util.ListIterator;

import cytoscape.data.synonyms.*;
import cytoscape.data.readers.*;

// -------------------------------------------------------------------------
public class ThesaurusFlatFileReader {
	Thesaurus thesaurus;
	String fullText;
	String[] lines;

	boolean flip;

	// -------------------------------------------------------------------------
	public ThesaurusFlatFileReader(String filename) throws Exception {
		flip = false;
		try {
			if (filename.trim().startsWith("jar://")) {
				TextJarReader reader = new TextJarReader(filename);
				reader.read();
				fullText = reader.getText();
			} else if (filename.trim().startsWith("http://")) {
				TextHttpReader reader = new TextHttpReader(filename);
				reader.read();
				fullText = reader.getText();
			} else {
				TextFileReader reader = new TextFileReader(filename);
				reader.read();
				fullText = reader.getText();
			}
		} catch (Exception e0) {
			System.err.println("-- Exception while reading ontology flat file "
					+ filename);
			System.err.println(e0.getMessage());
			return;
		}

		read();
	}

	public ThesaurusFlatFileReader(final BufferedReader rd, boolean isFlip)
			throws Exception {
		Vector extractedLines = null;

		// Flip or not
		flip = isFlip;

		fullText = null;
		extractedLines = new Vector();

		String curLine = null;

		while (null != (curLine = rd.readLine())) {
			extractedLines.add(curLine);
			// System.out.println( curLine );
		}
		rd.close();

		Object[] entireFile = extractedLines.toArray();
		lines = new String[entireFile.length];
		try {
			System.arraycopy(entireFile, 0, lines, 0, lines.length);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw e;
		}

		read();
	}

	// -------------------------------------------------------------------------
	private void read() throws Exception {
		if (fullText != null) {
			lines = fullText.split("\n");
		}

		String species = lines[0].trim();
		//System.out.println("Thesaurus reader found taxon: " + species);
		thesaurus = new Thesaurus(species);

		for (int i = 1; i < lines.length; i++) {
			String line = lines[i].trim();

			// If comment line or too short, just skip
			if (line.startsWith("#"))
				continue;
			if (line.length() < 3)
				continue;
			
			String[] tokens = lines[i].split("\\s+", 0);
			if (tokens.length < 2)
				continue;
			
			
			String canonicalName = tokens[0].trim();
			String commonName = tokens[1].trim();
			if (canonicalName.length() == 0)
				continue;
			if (commonName.length() == 0)
				continue;
			
			// The following is for "Flipping, but this does not work correctly
			// now, we need to re-write
			// the entire Reader system...
			if (flip == false) {
				thesaurus.add(canonicalName, commonName);
//				System.out.println("Cannonical = " + canonicalName
//						+ "     Common 1 = " + commonName);
				for (int t = 2; t < tokens.length; t++) {
					thesaurus.addAlternateCommonName(canonicalName, tokens[t]
							.trim());
					// System.out.println(" Cannonical = " + canonicalName + "
					// Common " + t + " = " + tokens [t].trim() );
				} // for i
			} else if (flip == true ){
				// If flip is true, swap the fields.
				
				thesaurus.add(commonName, canonicalName);
//
//				for (int t = 2; t < tokens.length; t++) {
//					thesaurus.addAlternateCommonName(tokens[t].trim(),
//							canonicalName);
//					System.out.println("Cannonical = " + canonicalName
//							+ "    Common " + t + " = " + tokens[t].trim());
//				} // for i
			}

		}

	} // read
	// -------------------------------------------------------------------------

	public Thesaurus getThesaurus() {
		return thesaurus;
	}
	// -------------------------------------------------------------------------
} // class ThesaurusFlatFileReader

