//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;
//----------------------------------------------------------------------------
import java.util.Map;
import javax.swing.JPanel;

import y.base.Edge;
import y.view.LineType;

import cytoscape.data.CyNetwork;
//----------------------------------------------------------------------------
public interface EdgeLineTypeCalculator extends Calculator{
    
    LineType calculateEdgeLineType(Edge edge, CyNetwork network);
}

