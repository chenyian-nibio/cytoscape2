//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.visual.calculators;
//--------------------------------------------------------------------------
import cytoscape.data.CyNetwork;
import y.base.Node;
//--------------------------------------------------------------------------
public interface NodeFontSizeCalculator extends Calculator {
    public float calculateNodeFontSize(Node node, CyNetwork network);
}
