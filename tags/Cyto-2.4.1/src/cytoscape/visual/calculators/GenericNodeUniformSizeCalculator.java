/*
  File: GenericNodeUniformSizeCalculator.java

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

//----------------------------------------------------------------------------
// $Revision: 8189 $
// $Date: 2006-09-13 13:51:38 -0700 (Wed, 13 Sep 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.NodeAppearance;

import cytoscape.visual.mappings.ObjectMapping;

import cytoscape.visual.parsers.DoubleParser;

import cytoscape.visual.ui.VizMapUI;

import giny.model.Node;

//----------------------------------------------------------------------------
import java.util.Map;
import java.util.Properties;

import javax.swing.JPanel;


//----------------------------------------------------------------------------
/**
 *
 */
public class GenericNodeUniformSizeCalculator extends GenericNodeSizeCalculator
    implements NodeSizeCalculator {
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public byte getType() {
		return VizMapUI.NODE_SIZE;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getPropertyLabel() {
		return "nodeUniformSizeCalculator";
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getTypeName() {
		return "Node Size";
	}

    /**
     * Used to set the various properties throughout the hierarchy.
     */
    private void set() {
        set( VizMapUI.NODE_SIZE, "nodeUniformSizeCalculator", "Node Size" );
    }

	GenericNodeUniformSizeCalculator() {
		super();
		set();
	}

	/**
	 * Creates a new GenericNodeUniformSizeCalculator object.
	 *
	 * @param name  DOCUMENT ME!
	 * @param m  DOCUMENT ME!
	 */
	public GenericNodeUniformSizeCalculator(String name, ObjectMapping m) {
		super(name, m);
		set();
	}

	/**
	 * Creates a new GenericNodeUniformSizeCalculator object.
	 *
	 * @param name  DOCUMENT ME!
	 * @param props  DOCUMENT ME!
	 * @param baseKey  DOCUMENT ME!
	 */
	public GenericNodeUniformSizeCalculator(String name, Properties props, String baseKey) {
		super(name, props, baseKey);
		set();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param appr DOCUMENT ME!
	 * @param node DOCUMENT ME!
	 * @param network DOCUMENT ME!
	 */
	public void apply(NodeAppearance appr, Node node, CyNetwork network) {
		apply(appr, node, network, SIZE);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 * @param n DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double calculateNodeSize(Node e, CyNetwork n) {
		NodeAppearance ea = new NodeAppearance();
		apply(ea, e, n);

		return ea.getSize();
	}
}