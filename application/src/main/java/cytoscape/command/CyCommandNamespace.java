/*
File: CyCommandNamespace.java

Copyright (c) 2009, The Cytoscape Consortium (www.cytoscape.org)

The Cytoscape Consortium is:
- Institute for Systems Biology
- University of California San Diego
- Memorial Sloan-Kettering Cancer Center
- Institut Pasteur
- Agilent Technologies
- University of California San Francisco

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
package cytoscape.command;

/**
 * A CyCommandNamespace represents a reservation for a group of {@link CyCommand}s.  
 * A namespace reservation must be secured before a client can register commands
 * to be executed.
 */
public interface CyCommandNamespace {
	/**
	 * Return the string name that is held by this namespace.
	 *
	 * @return the name reserved by this namespace.
	 */
	String getNamespaceName();

	/**
 	 * Check to see if this namespace supports a particular version of the command handlers.
 	 * The version is a floating-point value of the form major.minor where minor is assumed to
 	 * be two digits.  So, a version of 1.1 should be thought of as 1.10, while version 1.01 is
 	 * the first minor version of major version 1.
 	 *
 	 * @param version the version to check for support
 	 * @return true if this namespace supports the requested version
 	 */
	boolean supportsVersion(float version);
}
