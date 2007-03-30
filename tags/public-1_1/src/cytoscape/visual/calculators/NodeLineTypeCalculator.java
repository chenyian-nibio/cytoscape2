//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;
//----------------------------------------------------------------------------
import java.util.Map;
import javax.swing.JPanel;

import y.base.Node;
import y.view.LineType;

import cytoscape.visual.Network;
//----------------------------------------------------------------------------
public interface NodeLineTypeCalculator extends Calculator {
    
    LineType calculateNodeLineType(Node node, Network network);
}
