package cytoscape.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.actions.CreateNetworkViewAction;
import cytoscape.data.FlagEvent;
import cytoscape.data.FlagEventListener;
import cytoscape.util.CyNetworkNaming;
import cytoscape.util.swing.AbstractTreeTableModel;
import cytoscape.util.swing.JTreeTable;
import cytoscape.util.swing.TreeTableModel;

public class NetworkPanel extends JPanel implements PropertyChangeListener,
		TreeSelectionListener, FlagEventListener {

	protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(
			this);

	JTreeTable treeTable;
	NetworkTreeNode root;
	JPanel navigatorPanel;
	JPopupMenu popup;
	PopupActionListener popupActionListener;
	JMenuItem createViewItem;
	JMenuItem destroyViewItem;
	JMenuItem destroyNetworkItem;
	JMenuItem editNetworkTitle;
	JSplitPane split;
	NetworkTreeTableModel treeTableModel;

	private CytoscapeDesktop cytoscapeDesktop;

	public NetworkPanel(CytoscapeDesktop desktop) {
		super();
		this.cytoscapeDesktop = desktop;
		initialize();
	}

	protected void initialize() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(181, 700));
		root = new NetworkTreeNode("Network Root", "root");
		treeTableModel = new NetworkTreeTableModel(root);
		treeTable = new JTreeTable(treeTableModel);
		treeTable.getTree().addTreeSelectionListener(this);
		treeTable.getTree().setRootVisible(false);

		// ToolTipManager.sharedInstance().registerComponent(treeTable.getTree());
		ToolTipManager.sharedInstance().registerComponent(treeTable);

		treeTable.getTree().setCellRenderer(new MyRenderer());

		treeTable.getColumn("Network").setPreferredWidth(100);
		treeTable.getColumn("Nodes").setPreferredWidth(45);
		treeTable.getColumn("Edges").setPreferredWidth(45);

		// treeTable.setMaximumSize( new Dimension( 150, ) );
		navigatorPanel = new JPanel();
		navigatorPanel.setMinimumSize(new Dimension(180, 180));
		navigatorPanel.setMaximumSize(new Dimension(180, 180));
		navigatorPanel.setPreferredSize(new Dimension(180, 180));

		JScrollPane scroll = new JScrollPane(treeTable);
		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroll,
				navigatorPanel);
		split.setResizeWeight(1);
		add(split);

		// this mouse listener listens for the right-click event and will show
		// the pop-up
		// window when that occurrs
		treeTable.addMouseListener(new PopupListener());

		// create and populate the popup window
		popup = new JPopupMenu();
		editNetworkTitle = new JMenuItem(PopupActionListener.EDIT_TITLE);
		createViewItem = new JMenuItem(PopupActionListener.CREATE_VIEW);
		destroyViewItem = new JMenuItem(PopupActionListener.DESTROY_VIEW);
		destroyNetworkItem = new JMenuItem(PopupActionListener.DESTROY_NETWORK);

		// action listener which performs the tasks associated with the popup
		// listener
		popupActionListener = new PopupActionListener();
		editNetworkTitle.addActionListener(popupActionListener);
		createViewItem.addActionListener(popupActionListener);
		destroyViewItem.addActionListener(popupActionListener);
		destroyNetworkItem.addActionListener(popupActionListener);
		popup.add(editNetworkTitle);
		popup.add(createViewItem);
		popup.add(destroyViewItem);
		popup.add(destroyNetworkItem);

	}

	public void setNavigator(JComponent comp) {
		split.setRightComponent(comp);
		split.validate();
	}
	
	/**
	 * This is used by Session writer.
	 * 
	 * @return 
	 */
	public JTreeTable getTreeTable() {
		return treeTable;
	}

	public JPanel getNavigatorPanel() {
		return navigatorPanel;
	}

	public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
		return pcs;
	}

	public void removeNetwork(String network_id) {

		NetworkTreeNode node = getNetworkNode(network_id);
		Enumeration children = node.children();
		NetworkTreeNode child = null;
		ArrayList removed_children = new ArrayList();
		while (children.hasMoreElements()) {
			removed_children.add(children.nextElement());
		}

		for (Iterator i = removed_children.iterator(); i.hasNext();) {
			child = (NetworkTreeNode) i.next();
			child.removeFromParent();
			root.add(child);
		}
		Cytoscape.getNetwork(network_id).removeFlagEventListener(this);
		node.removeFromParent();
		treeTable.getTree().collapsePath(new TreePath(new TreeNode[] { root }));
		treeTable.getTree().updateUI();
		treeTable.doLayout();

	}

	// JBK added updateTitle
	// 11-13-05
	/**
	 * update a network title
	 */
	public void updateTitle(CyNetwork network) {
		// updates the title in the network panel
		treeTableModel.setValueAt(network.getTitle(), treeTable.getTree()
				.getSelectionPath().getLastPathComponent(), 0);
		treeTable.getTree().updateUI();
		treeTable.doLayout();
		// updates the title in the networkViewMap
		NetworkViewManager netViewManager = Cytoscape.getDesktop()
				.getNetworkViewManager();
		netViewManager.updateNetworkTitle(network);
	}

	public void onFlagEvent(FlagEvent event) {
		treeTable.getTree().updateUI();
	}

	public void addNetwork(String network_id, String parent_id) {
		// first see if it exists
		if (getNetworkNode(network_id) == null) {
			NetworkTreeNode dmtn = new NetworkTreeNode(Cytoscape.getNetwork(
					network_id).getTitle(), network_id);
			Cytoscape.getNetwork(network_id).addFlagEventListener(this);
			if (parent_id != null) {
				NetworkTreeNode parent = getNetworkNode(parent_id);
				parent.add(dmtn);
			} else {
				root.add(dmtn);
			}

			treeTable.getTree().collapsePath(
					new TreePath(new TreeNode[] { root }));
			treeTable.getTree().updateUI();
			TreePath path = new TreePath(dmtn.getPath());
			treeTable.getTree().expandPath(path);
			treeTable.getTree().scrollPathToVisible(path);
			treeTable.doLayout();
		}
	}

	public void focusNetworkNode(String network_id) {
		DefaultMutableTreeNode node = getNetworkNode(network_id);
		if (node != null) {
			treeTable.getTree().getSelectionModel().setSelectionPath(
					new TreePath(node.getPath()));
			treeTable.getTree().scrollPathToVisible(
					new TreePath(node.getPath()));
		}
	}

	public NetworkTreeNode getNetworkNode(String network_id) {

		Enumeration tree_node_enum = root.breadthFirstEnumeration();
		while (tree_node_enum.hasMoreElements()) {
			NetworkTreeNode node = (NetworkTreeNode) tree_node_enum
					.nextElement();
			if ((String) node.getNetworkID() == network_id) {
				return node;
			}
		}
		return null;
	}

	public void fireFocus(String network_id) {
		pcs.firePropertyChange(new PropertyChangeEvent(this,
				CytoscapeDesktop.NETWORK_VIEW_FOCUS, null, network_id));
	}

	public void valueChanged(TreeSelectionEvent e) {
		NetworkTreeNode node = (NetworkTreeNode) treeTable.getTree()
				.getLastSelectedPathComponent();

		if (node == null)
			return;
		if (node.getUserObject() == null)
			return;
		fireFocus((String) node.getNetworkID());

	}

	public void propertyChange(PropertyChangeEvent e) {

		if (e.getPropertyName() == Cytoscape.NETWORK_CREATED) {
			addNetwork((String) e.getNewValue(), (String) e.getOldValue());
		}

		if (e.getPropertyName() == Cytoscape.NETWORK_DESTROYED) {
			removeNetwork((String) e.getNewValue());
		}

		else if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUSED) {
			focusNetworkNode((String) e.getNewValue());
		}

	}

	/**
	 * Inner class that extends the AbstractTreeTableModel
	 */
	class NetworkTreeTableModel extends AbstractTreeTableModel {

		String[] columns = { "Network", "Nodes", "Edges" };
		Class[] columns_class = { TreeTableModel.class, String.class,
				String.class };

		public NetworkTreeTableModel(Object root) {
			super(root);
		}

		public Object getChild(Object parent, int index) {
			Enumeration tree_node_enum = ((DefaultMutableTreeNode) getRoot())
					.breadthFirstEnumeration();
			while (tree_node_enum.hasMoreElements()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree_node_enum
						.nextElement();
				if (node == parent) {
					return node.getChildAt(index);
				}
			}
			return null;
		}

		public int getChildCount(Object parent) {
			Enumeration tree_node_enum = ((DefaultMutableTreeNode) getRoot())
					.breadthFirstEnumeration();
			while (tree_node_enum.hasMoreElements()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree_node_enum
						.nextElement();
				if (node == parent) {
					return node.getChildCount();
				}
			}
			return 0;
		}

		public int getColumnCount() {
			return columns.length;

		}

		public String getColumnName(int column) {
			return columns[column];
		}

		public Class getColumnClass(int column) {
			return columns_class[column];
		}

		public Object getValueAt(Object node, int column) {
			if (column == 0)
				return ((DefaultMutableTreeNode) node).getUserObject();
			else if (column == 1) {
				// return new Integer( Cytoscape.getNetwork( ( ( NetworkTreeNode
				// )node).getNetworkID() ).getNodeCount() );
				CyNetwork cyNetwork = Cytoscape
						.getNetwork(((NetworkTreeNode) node).getNetworkID());
				return "" + cyNetwork.getNodeCount() + "("
						+ cyNetwork.getFlaggedNodes().size() + ")";
			} else if (column == 2) {
				CyNetwork cyNetwork = Cytoscape
						.getNetwork(((NetworkTreeNode) node).getNetworkID());
				return "" + cyNetwork.getEdgeCount() + "("
						+ cyNetwork.getFlaggedEdges().size() + ")";
			}
			return "";

		}

		// Brad
		public void setValueAt(Object aValue, Object node, int column) {
			if (column == 0) {
				((DefaultMutableTreeNode) node).setUserObject(aValue);
			} else
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						"Error: assigning value at in NetworkPanel");
			// This function is not used to set node and edge values.
		}

	} // NetworkTreeTableModel

	public class NetworkTreeNode extends DefaultMutableTreeNode {

		protected String network_uid;

		public NetworkTreeNode(Object userobj, String id) {
			super(userobj.toString());
			network_uid = id;
		}

		protected void setNetworkID(String id) {
			network_uid = id;
		}

		protected String getNetworkID() {
			return network_uid;
		}
	}

	private class MyRenderer extends DefaultTreeCellRenderer {
		Icon tutorialIcon;

		public MyRenderer() {

		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);

			if (hasView(value)) {
				// setIcon(tutorialIcon);
				setBackgroundNonSelectionColor(java.awt.Color.green.brighter());
				setBackgroundSelectionColor(java.awt.Color.green.darker());
			} else {
				setBackgroundNonSelectionColor(java.awt.Color.red.brighter());
				setBackgroundSelectionColor(java.awt.Color.red.darker());

			}

			return this;
		}

		private boolean hasView(Object value) {

			NetworkTreeNode node = (NetworkTreeNode) value;
			setToolTipText(Cytoscape.getNetwork(node.getNetworkID()).getTitle());
			return Cytoscape.viewExists(node.getNetworkID());

		}

	}

	/**
	 * This class listens to mouse events from the TreeTable, if the mouse event
	 * is one that is canonically associated with a popup menu (ie, a right
	 * click) it will pop up the menu with option for destroying view, creating
	 * view, and destroying network (this is platform specific apparently)
	 */
	protected class PopupListener extends MouseAdapter {
		/**
		 * Don't know why you need both of these, but this is how they did it in
		 * the example
		 */
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		/**
		 * Don't know why you need both of these, but this is how they did it in
		 * the example
		 */
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		/**
		 * if the mouse press is of the correct type, this function will maybe
		 * display the popup
		 */
		private void maybeShowPopup(MouseEvent e) {
			// check for the popup type
			if (e.isPopupTrigger()) {
				// get the row where the mouse-click originated
				int row = treeTable.rowAtPoint(e.getPoint());
				if (row != -1) {
					JTree tree = treeTable.getTree();
					TreePath treePath = tree.getPathForRow(row);
					String networkID = (String) ((NetworkTreeNode) treePath
							.getLastPathComponent()).getNetworkID();

					CyNetwork cyNetwork = Cytoscape.getNetwork(networkID);
					if (cyNetwork != null) {
						// disable or enable specific options with respect to
						// the actual network
						// that is selected
						if (Cytoscape.viewExists(networkID)) {
							// disable the view creation item
							createViewItem.setEnabled(false);
							destroyViewItem.setEnabled(true);
						} // end of if ()
						else {
							createViewItem.setEnabled(true);
							destroyViewItem.setEnabled(false);
						} // end of else
						// let the actionlistener know which network it should
						// be operating
						// on when (if) it is called
						popupActionListener.setActiveNetwork(cyNetwork);
						// display the popup
						popup.show(e.getComponent(), e.getX(), e.getY());
					}

				}
			}
		}
	}

}

/**
 * This class listens for actions from the popup menu, it is responsible for
 * performing actions related to destroying and creating views, and destroying
 * the network.
 */
class PopupActionListener implements ActionListener {
	/**
	 * Constants for JMenuItem labels
	 */
	public static String DESTROY_VIEW = "Destroy View";
	public static String CREATE_VIEW = "Create View";
	public static String DESTROY_NETWORK = "Destroy Network";
	public static String EDIT_TITLE = "Edit Network Title";

	/**
	 * This is the network which originated the mouse-click event (more
	 * appropriately, the network associated with the ID associated with the row
	 * associated with the JTable that originated the popup event
	 */
	protected CyNetwork cyNetwork;

	/**
	 * Based on the action event, destroy or create a view, or destroy a network
	 */
	public void actionPerformed(ActionEvent ae) {
		String label = ((JMenuItem) ae.getSource()).getText();
		// Figure out the appropriate action
		if (label == DESTROY_VIEW) {
			Cytoscape.destroyNetworkView(cyNetwork);
		} // end of if ()
		else if (label == CREATE_VIEW) {
			CreateNetworkViewAction.createViewFromCurrentNetwork(cyNetwork);
		} // end of if ()
		else if (label == DESTROY_NETWORK) {
			Cytoscape.destroyNetwork(cyNetwork);
		} // end of if ()
		else if (label == EDIT_TITLE) {
			CyNetworkNaming.editNetworkTitle(cyNetwork);
			Cytoscape.getDesktop().getNetworkPanel().updateTitle(cyNetwork);
		} // end of if ()
		else {
			// throw an exception here?
			System.err.println("Unexpected network panel popup option");
		} // end of else
	}

	/**
	 * Right before the popup menu is displayed, this function is called so we
	 * know which network the user is clicking on to call for the popup menu
	 */
	public void setActiveNetwork(CyNetwork cyNetwork) {
		this.cyNetwork = cyNetwork;
	}
}
