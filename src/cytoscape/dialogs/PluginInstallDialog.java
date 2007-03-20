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

import cytoscape.Cytoscape;

import cytoscape.plugin.PluginInfo;
import cytoscape.plugin.PluginManager;

import cytoscape.util.IndeterminateProgressBar;
import cytoscape.util.SwingWorker;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;


/**
 *
 */
public class PluginInstallDialog extends JDialog implements TreeSelectionListener {
	private JEditorPane infoPanel;
	private JTree tree;
	private JScrollPane treeScroll;
	private DefaultMutableTreeNode rootNode;
	private JPanel buttonPanel;
	private JButton installButton;
	private JButton closeButton;
	private JButton changeSite;
	protected static int InstallStopped = 0;

	/**
	 * Creates a new PluginInstallDialog object.
	 *
	 * @throws HeadlessException  DOCUMENT ME!
	 */
	public PluginInstallDialog() throws HeadlessException {
		super(Cytoscape.getDesktop(), "Install Plugins");
		this.setLocationRelativeTo(Cytoscape.getDesktop());
		this.initDialog();
	}

	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e) { // displays the info for each plugin

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
		                                                                                                 .getLastSelectedPathComponent();

		if (node == null)
			return;

		Object nodeInfo = node.getUserObject();

		if (node.isLeaf()) {
			PluginInfo info = (PluginInfo) nodeInfo;
			displayInfo(info);
		}
	}

	private void displayInfo(PluginInfo obj) {
		infoPanel.setText(obj.prettyOutput());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param CategoryName DOCUMENT ME!
	 * @param Plugins DOCUMENT ME!
	 * @param index DOCUMENT ME!
	 */
	public void addCategory(String CategoryName, List<PluginInfo> Plugins, int index) {
		DefaultMutableTreeNode Category = new DefaultMutableTreeNode(CategoryName);
		rootNode.insert(Category, index);

		Iterator<PluginInfo> pI = Plugins.iterator();
		int i = 0;

		while (pI.hasNext()) {
			PluginInfo CurrentPlugin = pI.next();
			Category.insert(new DefaultMutableTreeNode(CurrentPlugin), i);
			i++;
		}
	}

	private void initDialog() {
		setPreferredSize(new Dimension(700, 500));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new GridBagLayout());

		initTree();
		initSplit();
		initButtons();
	}

	/*
	 * Set up the tree/info pane widgets for plugin categories
	 */
	private void initTree() {
		treeScroll = new JScrollPane(tree);
		tree = new JTree();
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		// Set up tree
		tree.addTreeSelectionListener(this); // give tree a listener
		                                     // would rather this root was not visible

		rootNode = new DefaultMutableTreeNode("Cytoscape Plugin Categories");

		DefaultTreeModel model = new DefaultTreeModel(rootNode);
		tree.setModel(model);

		// tree.setRootVisible(false);
		JLabel Label = new JLabel("Cytoscape Plugins By Category");
		Label.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 24));
		treeScroll.add(Label);
		treeScroll.setViewportView(tree);
		treeScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		infoPanel = new JEditorPane();
		infoPanel.setEditable(false);

		// label panel
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(10, 0, 10, 0);
		getContentPane().add(new JLabel("Cytoscape Plugins"), gridBagConstraints);
	}

	/*
	 * Set up split pane for the tree and info panels
	 */
	private void initSplit() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();

		// set up split panel
		JSplitPane Split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		Split.setLeftComponent(treeScroll);
		Split.setRightComponent(infoPanel);
		// add split panel
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new Insets(0, 10, 20, 10);
		getContentPane().add(Split, gridBagConstraints);
	}

	/*
	 * Sets up install/cancel buttons
	 */
	private void initButtons() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		// set up button panel
		buttonPanel = new JPanel(new GridBagLayout());
		gridBagConstraints.insets = new Insets(5, 0, 0, 5);

		installButton = new JButton("Install");
		installButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent Event) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
					                                                                                                                                                                                                    .getLastSelectedPathComponent();
					System.out.println(node.toString());

					if (node == null) {
						// error
						System.err.println("Node was null, this is bad...");

						return;
					}

					Object nodeInfo = node.getUserObject();

					// TODO should have a dialog of some sort during the install
					if (node.isLeaf()) {
						final PluginInfo info = (PluginInfo) nodeInfo;
						SwingWorker worker = getWorker(info, PluginInstallDialog.this);
						worker.start();
					}
				}
			});
		PluginInstallDialog.InstallStopped = 0;

		installButton.setPreferredSize(new Dimension(81, 23));
		buttonPanel.add(installButton, gridBagConstraints);
		closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent Event) {
					dispose();
				}
			});
		closeButton.setPreferredSize(new Dimension(81, 23));
		buttonPanel.add(closeButton, gridBagConstraints);

		changeSite = new JButton("Change Download Site");
		changeSite.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent Event) {
					// bring up the bookmarks dialog, choose/add a url?
					JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					                              "Currently Not Implemented");
				}
			});
		buttonPanel.add(changeSite, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.insets = new Insets(10, 0, 10, 0);
		getContentPane().add(buttonPanel, gridBagConstraints);
	}

	/*
	 * Creates the swing worker that displays progress bar during install
	 */
	private SwingWorker getWorker(PluginInfo Info, JDialog Owner) {
		final PluginInfo info = Info;
		final JDialog Dialog = Owner;
		SwingWorker worker = new SwingWorker() {
			public Object construct() {
				GridBagConstraints gridBagConstraints = new GridBagConstraints();
				final PluginManager Mgr = PluginManager.getPluginManager();
				final IndeterminateProgressBar InstallBar = new IndeterminateProgressBar(Dialog,
				                                                                         "Installing Plugin",
				                                                                         info
				                                                                                                                                                                                                                                                                       .getName()
				                                                                         + " installation in progress...");
				InstallBar.setLayout(new GridBagLayout());

				JButton CancelInstall = new JButton("Cancel Install");
				CancelInstall.setSize(new Dimension(81, 23));
				CancelInstall.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent E) {
							InstallBar.dispose();
							Mgr.abortInstall();
						}
					});
				gridBagConstraints.gridy = 2;
				InstallBar.add(CancelInstall, gridBagConstraints);
				InstallBar.pack();
				InstallBar.setLocationRelativeTo(Dialog);
				InstallBar.setVisible(true);

				boolean installOk = Mgr.install(info);
				InstallBar.dispose();

				if (installOk) {
					JOptionPane.showMessageDialog(Dialog,
					                              "Plugin '" + info.getName()
					                              + "' installed.  You will need to restart Cytoscape to use this plugin.",
					                              "Installation Complete", JOptionPane.PLAIN_MESSAGE);
				}

				return null;
			}
		};

		return worker;
	}
}
