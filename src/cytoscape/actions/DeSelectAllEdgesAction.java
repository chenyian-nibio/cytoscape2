//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import phoebe.*;

import phoebe.util.*;
import giny.model.*;
import giny.view.*;
import java.util.*;
import edu.umd.cs.piccolo.*;


import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class DeSelectAllEdgesAction extends AbstractAction  {

    NetworkView networkView;

    public DeSelectAllEdgesAction(NetworkView networkView) {
        super ("Deselect all edges");
        this.networkView = networkView;

    }
    

    public void actionPerformed (ActionEvent e) {
	if (networkView.getCytoscapeObj().getConfiguration().isYFiles()) {    
	  //not implemented for y files
	}
	else { // using giny
		
			//GinyUtils.deselectAllNodes(networkView.getView());
			GinyUtils.deselectAllEdges(networkView.getView());
		
	}//!Yfiles
			
		
    }//action performed

}

