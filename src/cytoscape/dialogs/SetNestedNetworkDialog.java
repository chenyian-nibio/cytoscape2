/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.dialogs;

import giny.view.NodeView;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import java.util.Set;
import java.util.ArrayList;
import javax.swing.ListCellRenderer;
import java.awt.Color;
/**
 *
 */
public class SetNestedNetworkDialog extends JDialog implements ListSelectionListener {
	/**
	 * Creates a new EditNetworkTitleDialog object.
	 *
	 * @param parent  DOCUMENT ME!
	 * @param modal  DOCUMENT ME!
	 * @param pName  DOCUMENT ME!
	 */
	NodeView nodeView;
	
	public SetNestedNetworkDialog(JFrame parent, boolean modal, NodeView nodeView) {
		super(parent, "Set Nested Network for " + nodeView.getNode().getIdentifier(), modal);
		init(nodeView);
	}
	
	public SetNestedNetworkDialog(JFrame parent, boolean modal) {
		super(parent, "Set Nested Network for new node", modal);
		init(null);
	}
	
	private void init(NodeView nodeView){
		this.nodeView = nodeView;
        
        initComponents();
        this.lstNetwork.addListSelectionListener(this);
        this.lstNetwork.setCellRenderer(new MyCellRenderer());
        this.btnOK.setEnabled(false);
        
        Set<CyNetwork> networkSet = Cytoscape.getNetworkSet();
        networkSet.remove(Cytoscape.getCurrentNetwork());
        
        Object[] objs = networkSet.toArray();
              
        CyNetwork[] networkArray = new CyNetwork[objs.length];
        for (int i=0; i< networkArray.length; i++){
        	networkArray[i] = (CyNetwork) objs[i];
        }
        
        this.lstNetwork.setListData(networkArray);
        
		setSize(new java.awt.Dimension(500, 300));				
	}
	
	
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lbTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstNetwork = new javax.swing.JList();
        pnlButtons = new javax.swing.JPanel();
        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        lbTitle.setFont(new java.awt.Font("Tahoma", 1, 14));
        lbTitle.setText("Please choose a network");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(lbTitle, gridBagConstraints);

        lstNetwork.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstNetwork.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(lstNetwork);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        btnOK.setText("OK");
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        pnlButtons.add(btnOK);

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        pnlButtons.add(btnCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        getContentPane().add(pnlButtons, gridBagConstraints);

        pack();
    }// </editor-fold>                        

	
    // Variables declaration - do not modify                     
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JList lstNetwork;
    private javax.swing.JPanel pnlButtons;
    // End of variables declaration                   
	
    private CyNetwork selectedNetwork = null;
    
    public CyNetwork getSelectedNetwork(){
    	return selectedNetwork;
    }
    
    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {                                      
    	selectedNetwork = (CyNetwork) this.lstNetwork.getSelectedValue();
    	if (this.nodeView != null) {
        	this.nodeView.getNode().setNestedNetwork(selectedNetwork);    		
    	}
    	this.dispose();
    }                                     

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {                                          
        this.dispose();
    }                                         

    
    public void valueChanged(ListSelectionEvent e) {
    	// DIsable the OK button if no row is selected in the list
    	if (this.lstNetwork.getSelectedIndex() == -1){
    		this.btnOK.setEnabled(false);
    	}
    	else {
    		this.btnOK.setEnabled(true);
    	}
    }
    

    class MyCellRenderer extends javax.swing.JLabel implements ListCellRenderer {

        public MyCellRenderer() {
            setOpaque(true);
        }
        public java.awt.Component getListCellRendererComponent(
            javax.swing.JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
        {
        	CyNetwork cyNetwork = (CyNetwork) value;
            setText(cyNetwork.getTitle());
            setBackground(isSelected ? SELECTED_CELL_COLOR: Color.white);
            setForeground(isSelected ? Color.BLACK : Color.black);
            return this;
        }
    }
    
	private static final Color SELECTED_CELL_COLOR = new Color(0, 100, 255, 40);
}
