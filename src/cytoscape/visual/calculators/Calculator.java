//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.visual.calculators;
//------------------------------------------------------------------------------
import javax.swing.JPanel;
import javax.swing.JDialog;
import cytoscape.visual.Network;
//------------------------------------------------------------------------------
/**
 *  Calculator specifies a super-interface for all calculator interfaces.
 *  <b>DO NOT</b> create classes that only implement Calculator! When writing
 *  calculators, you <b>MUST</b> extend one of {@link NodeCalculator} or
 *  {@link EdgeCalculator} and implement one of the 11 attribute calculator interfaces.
 */
public interface Calculator extends Cloneable {
    /**
     *	Get the UI for a calculator.
     *
     *	@param	parent	Parent JDialog for the UI
     *	@param	network	Network object containing underlying graph data
     */
    JPanel getUI(JDialog parent, Network network);

    /**
     *  Gets calculator name.
     */
    public String toString();

    /**
     *  Set calculator name. <b>DO NOT CALL THIS METHOD</b> unless you first get a valid
     *  name from the CalculatorCatalog. Even if you have a guaranteed valid name from
     *  the CalculatorCatalog, it is still preferrable to use the renameCalculator method
     *	in the CalculatorCatalog.
     */
    public void setName(String newName);

    /**
     *  Clone the calculator.
     */
    public Object clone() throws CloneNotSupportedException;
}
