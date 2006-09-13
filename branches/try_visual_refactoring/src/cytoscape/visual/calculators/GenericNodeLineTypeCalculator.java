
/*
  File: GenericNodeLineTypeCalculator.java 
  
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
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;
//----------------------------------------------------------------------------
import java.util.Map;
import java.util.Properties;
import javax.swing.JPanel;

import giny.model.Node;
import cytoscape.visual.LineType;

import cytoscape.CyNetwork;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.LineTypeParser;

import cytoscape.visual.NodeAppearance;
import cytoscape.visual.ui.VizMapUI;
//----------------------------------------------------------------------------
public class GenericNodeLineTypeCalculator extends NodeCalculator {


    public byte getType() {
        return VizMapUI.NODE_LINETYPE;
    }

    public String getPropertyObjectString() {
        return "";
    }

    public String getPropertyLabel() {
        return "nodeLineTypeCalculator";
    }
    
    public GenericNodeLineTypeCalculator(String name, ObjectMapping m) {
	super(name, m);

        Class c = null;
	//c = LineType.class;  // this line won't obfuscate; the one below does.
	c = LineType.LINE_1.getClass();
        if (!c.isAssignableFrom(m.getRangeClass()) ) {
            String s = "Invalid Calculator: Expected class " + c.toString()
                    + ", got " + m.getRangeClass().toString();
            throw new ClassCastException(s);
        }
    }
    /**
     * Constructor for dynamic creation via properties.
     */
    public GenericNodeLineTypeCalculator(String name, Properties props, String baseKey) {
        super(name, props, baseKey, new LineTypeParser(), LineType.LINE_1);
    }
    
    public void apply(NodeAppearance appr, Node node, CyNetwork network) {
        String canonicalName = node.getIdentifier();
        Map attrBundle = getAttrBundle(canonicalName);
		// add generic "ID" attribute
		attrBundle.put(AbstractCalculator.ID, node.getIdentifier());
        appr.setBorderLineType( (LineType)super.getMapping(0).calculateRangeValue(attrBundle) );
    }
}

