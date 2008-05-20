/*
  File: getCanonicalName.java

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

// getCanonicalName

//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers;

import cytoscape.data.annotation.*;
import cytoscape.logger.CyLogger;

import java.io.*;

//-----------------------------------------------------------------------------------
import java.rmi.*;

import java.util.Vector;


//------------------------------------------------------------------------------
/**
 *
 */
public class getCanonicalName {
	//------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 4) {
			CyLogger.getLogger().warn("usage:  getCanonicalName <rmi host name> <rmi service name> \"<species>\" <canonicalName>");
			System.exit(1);
		}

		String hostname = args[0];
		String serviceName = args[1];
		String species = args[2];
		String canonicalName = args[3];

		String serverName = "rmi://" + hostname + "/" + serviceName;
		BioDataServer server = new BioDataServer(serverName);
		CyLogger.getLogger().info(canonicalName + " -> " + server.getCanonicalName(species, canonicalName));
	} // main
	  //------------------------------------------------------------------------------
} // getCanonicalName
