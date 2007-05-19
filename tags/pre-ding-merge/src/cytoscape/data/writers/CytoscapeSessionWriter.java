/*
 File: CytoscapeSessionWriter.java 
 
 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
 
 The Cytoscape Consortium is: 
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Pasteur Institute
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

package cytoscape.data.writers;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.generated.Child;
import cytoscape.generated.Cysession;
import cytoscape.generated.Cytopanel;
import cytoscape.generated.Cytopanels;
import cytoscape.generated.HiddenEdges;
import cytoscape.generated.HiddenNodes;
import cytoscape.generated.Network;
import cytoscape.generated.NetworkTree;
import cytoscape.generated.Node;
import cytoscape.generated.ObjectFactory;
import cytoscape.generated.Panel;
import cytoscape.generated.Panels;
import cytoscape.generated.Parent;
import cytoscape.generated.Plugins;
import cytoscape.generated.SelectedEdges;
import cytoscape.generated.SelectedNodes;
import cytoscape.generated.SessionNote;
import cytoscape.generated.SessionState;
import cytoscape.generated.ViewableNodes;
import cytoscape.util.ZipMultipleFiles;
import cytoscape.util.swing.JTreeTable;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.NetworkPanel;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.CalculatorIO;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;

/*
 * Write session states into files.
 * 
 * Basic functions of this class are: 1. Create network files 2. Create session
 * state file 3. Get properties file locations 4. Zip them into one session file
 * "*.cys"
 * 
 * @author kono
 * 
 */

public class CytoscapeSessionWriter {

	// Enumerate types (node & edge)
	public final int NODE = 1;
	public final int EDGE = 2;

	private static final String DEFAULT_VS_NAME = "default"; 
	
	// Number of Cytopanels. Currently, we have 3 panels.
	private static final int CYTOPANEL_COUNT = 3;

	// Number of setting files in the cys file.
	// For now, we have cysession.xml, vizmap.prop, and cytoscape.prop.
	private static final int SETTING_FILE_COUNT = 3;

	// Name of CySession file.
	private static final String CYSESSION_FILE_NAME = "cysession.xml";
	private static final String VIZMAP_FILE = "session_vizmap.props";
	private static final String CYPROP_FILE = "session_cytoscape.props";

	// Extension for the xgmml file
	private static final String XGMML_EXT = ".xgmml";

	// Package name generated by JAXB.
	// This file was created from "cysession.schema"
	private final String packageName = "cytoscape.generated";

	// Zip utility to compress/decompress multiple files
	private ZipMultipleFiles zipUtil;

	// Property files
	private File vizProp;
	private File cyProp;

	Properties prop;

	// Root of the network tree
	static final String TREE_ROOT = "root";

	// File name for the session
	String sessionFileName = null;

	String[] targetFiles;
	Set networks;

	HashMap networkMap;

	int networkCount;

	//
	// The following JAXB-generated objects are for CySession.xml file.
	//
	ObjectFactory factory;
	Cysession session;
	NetworkTree tree;
	SessionState sState;
	SessionNote sNote;
	// Networks in the tree
	List netList;
	Cytopanels cps;
	List cytopanel;

	Plugins plugins;
	List plugin;

	// Cysession elements
	NetworkTree netTree;

	File sessionFolder;
	String sessionDirName;

	HashMap viewMap = (HashMap) Cytoscape.getNetworkViewMap();
	HashMap visualStyleMap;

	//
	// Constructor.
	//
	public CytoscapeSessionWriter(String sessionName) {
		this.sessionFileName = sessionName;
		this.sessionDirName = null;

		// For now, session ID is time and date
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyy_MM_dd-HH_mm");
		// System.out.println("Session Time Stamp: " + df.format(date));

		// Create CySession file
		sessionDirName = "CytoscapeSession-" + df.format(date);

	}

	private void getGUIStates() {
		
	}

	/**
	 * Write currnt session to a cys file.
	 * 
	 */
	public void write() {

		// Get all networks in the session
		networks = new HashSet();
		networks = Cytoscape.getNetworkSet();
		networkCount = networks.size();

		networkMap = new HashMap();

		// Total number of files in the zip archive will be
		// number of networks + property files
		targetFiles = new String[networks.size() + SETTING_FILE_COUNT];

		//
		// First, write all network files as XGMML
		//
		Iterator netIterator = networks.iterator();
		int fileCounter = SETTING_FILE_COUNT;
		while (netIterator.hasNext()) {
			// Get Current Network and View
			CyNetwork network = (CyNetwork) netIterator.next();
			CyNetworkView view = Cytoscape.getNetworkView(network
					.getIdentifier());

			String curNetworkName = network.getTitle();
			String xgmmlFileName = curNetworkName + XGMML_EXT;

			targetFiles[fileCounter] = xgmmlFileName;
			fileCounter++;

			try {
				makeXGMML(xgmmlFileName, network, view);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 
		// Next, create CySession file to save states.
		//
		try {
			createCySession(sessionDirName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		targetFiles[0] = VIZMAP_FILE;
		targetFiles[1] = CYPROP_FILE;
		targetFiles[2] = CYSESSION_FILE_NAME;

		// Prepare property files for saving
		preparePropFiles();

		// Zip call files
		// zipUtil = new ZipMultipleFiles(sessionFileName, targetFiles);
		zipUtil = new ZipMultipleFiles(sessionFileName, targetFiles,
				sessionDirName);
		zipUtil.compress();

		// getSessionState();
	}

	// Initialize objects for data binding
	private void initObjectsForDataBinding() throws JAXBException {
		factory = new ObjectFactory();

		session = factory.createCysession();

		sNote = factory.createSessionNote();
		sNote
				.setValue("Note for this session will be inserted here.  This can be anything...");
		String testmessage = "Note for this session will be inserted here.  This can be anything...";
		session.setSessionNote(testmessage);

		tree = factory.createNetworkTree();
		sState = factory.createSessionState();
		session.setSessionState(sState);

		cps = getCytoPanelStates();
		// plugins = factory.createPlugins();

		netList = tree.getNetwork();
		// cytopanel = cps.getCytopanel();
		// plugin = plugins.getPlugin();
		sState.setPlugins(plugins);
		sState.setCytopanels(cps);

	}

	public void preparePropFiles() {
		// Prepare vizmap

		Cytoscape.firePropertyChange(Cytoscape.SAVE_VIZMAP_PROPS, null, null);

		VisualMappingManager vizmapper = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = vizmapper.getCalculatorCatalog();

		vizProp = new File(VIZMAP_FILE);
		CalculatorIO.storeCatalog(catalog, vizProp);

		// Prepare property file
		try {
			cyProp = new File(CYPROP_FILE);
			FileOutputStream output = new FileOutputStream(cyProp);
			prop = CytoscapeInit.getProperties();
			prop.store(output, "Cytoscape Property File");
		} catch (Exception ex) {
			System.out.println("Cytoscape.Props Write error");
			ex.printStackTrace();
		}

	}

	// Determine file location of the prop files
	public void makeXGMML(String xgmmlFile, CyNetwork network,
			CyNetworkView view) throws IOException {

		// TEST!
		XGMMLWriter wt = new XGMMLWriter(network, view);
		try {
			FileWriter fileWriter2 = new FileWriter(xgmmlFile);
			wt.write(fileWriter2);
			fileWriter2.close();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//
	// Create CySession.xml file.
	//
	public void createCySession(String sessionName) throws Exception {

		JAXBContext jc = null;

		// jc = JAXBContext.newInstance(packageName, this.getClass()
		// .getClassLoader());
		jc = JAXBContext.newInstance(packageName);

		initObjectsForDataBinding();
		session.setId(sessionName);
		getNetworkTree();
		session.setNetworkTree(tree);
		
		
		

		Marshaller m = jc.createMarshaller();

		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		// Write session file
		try {
			m.marshal(session, new FileOutputStream(CYSESSION_FILE_NAME));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Get information about the current session status.
	// This includes the following:
	// 1. List of networks opened/created by the user.
	// 2. Status of the network.
	// 3. relationship between network-attributes
	//
	// Build GML file into xml

	//
	// Extract current NetworkPanel state for saving.
	//
	private void getNetworkTree() throws Exception {

		// Tree table storeed in the Network Panel
		JTreeTable treeTable;

		// Get network panel
		CytoscapeDesktop cyDesktop = Cytoscape.getDesktop();
		NetworkPanel netPanel = cyDesktop.getNetworkPanel();

		// Make hash to ID <--> network name conversion

		// Get list of networks
		treeTable = netPanel.getTreeTable();
		Iterator itr = networks.iterator();

		// Visit each node in the tree
		while (itr.hasNext()) {

			CyNetwork network = (CyNetwork) itr.next();
			String networkID = network.getIdentifier();
			String networkName = network.getTitle();

			networkMap.put(networkName, networkID);

			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) netPanel
					.getNetworkNode(networkID).getParent();
		}

		if (treeTable != null) {
			// Extract root node in the tree
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) netPanel
					.getNetworkNode(TREE_ROOT);

			walkTree(root);

		} else
			System.out.println("Tree is empty.");
	}

	//
	// Visit all tree node and save the status in the model.
	//
	/**
	 * @param node
	 * @throws JAXBException
	 */
	protected void walkTree(DefaultMutableTreeNode node) throws JAXBException {

		// Check number of children for this node.
		int childCount = node.getChildCount();

		// Create Network object for this node.
		Network curNode = factory.createNetwork();
		curNode.setFilename(node.getUserObject().toString() + XGMML_EXT);
		curNode.setId(node.getUserObject().toString());
		// CyNetworkView curView = Cytoscape.getNetworkView((String)
		// networkMap.get(node
		// .getUserObject().toString()));
		CyNetwork curNet = Cytoscape.getNetwork((String) networkMap.get(node
				.getUserObject().toString()));
		CyNetworkView curView = Cytoscape
				.getNetworkView(curNet.getIdentifier());

		curView = (CyNetworkView) viewMap.get(curNet.getIdentifier());

		if (!node.getUserObject().toString().equals("Network Root")) {
			String visualStyleName = null;
			if(curView != null) {
				VisualStyle curVS = curView.getVisualStyle();
				if(curVS != null) {
					visualStyleName = curVS.getName();
				}
			}
			if (visualStyleName == null) {
				visualStyleName = DEFAULT_VS_NAME;
			}

//			System.out.println("Saving Association: " + curNode.getId()
//					+ " --> " + visualStyleName);
			curNode.setVisualStyle(visualStyleName);
		} else {
			curNode.setVisualStyle(DEFAULT_VS_NAME);
		}

		if (Cytoscape.getNetworkView((String) networkMap.get(node
				.getUserObject().toString())) == null) {
			curNode.setView(false);
		} else {
			curNode.setView(true);
		}

		Parent parent = null;
		parent = factory.createParent();
		if (node.getParent() == null) {
			parent.setId("NULL");
			curNode.setParent(parent);
		} else {
			// Set current network as the parent of child networks.

			DefaultMutableTreeNode curParent = (DefaultMutableTreeNode) node
					.getParent();
			parent.setId(curParent.getUserObject().toString());
			curNode.setParent(parent);
		}

		List children = curNode.getChild();

		for (int i = 0; i < childCount; i++) {

			// Exctract a network from the Network Panel.
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
					.getChildAt(i);

			// Create Child object
			Child childNetwork = factory.createChild();
			childNetwork.setId(child.getUserObject().toString());
			children.add(childNetwork);

			if (child.isLeaf()) {
				// Reached to the leaf of network tree.
				// Need to create leaf node here.
				Network leaf = factory.createNetwork();
				leaf.setFilename(child.getUserObject().toString() + XGMML_EXT);
				leaf.setId(child.getUserObject().toString());
				CyNetworkView leafView = Cytoscape
						.getNetworkView((String) networkMap.get(child
								.getUserObject().toString()));

				leafView = (CyNetworkView) viewMap.get(leafView.getNetwork()
						.getIdentifier());
				
				String leafVisualStyleName = null;
				if( leafView != null) {
					VisualStyle leafVS = leafView.getVisualStyle();
					if(leafVS != null) {
						leafVisualStyleName = leafVS.getName();
					}
				}
				
				if (leafVisualStyleName == null) {
					leafVisualStyleName = DEFAULT_VS_NAME;
				}
				leaf.setVisualStyle(leafVisualStyleName);
				System.out.println("Saving Association: " + leaf.getId()
						+ " --> " + leafVisualStyleName);

				String targetID = (String) networkMap.get(child.getUserObject()
						.toString());

				Parent tempParent = factory.createParent();
				tempParent.setId(curNode.getId());
				leaf.setParent(tempParent);

				CyNetwork targetNetwork = Cytoscape.getNetwork(targetID);
				CyNetworkView curNetworkView = Cytoscape
						.getNetworkView(targetID);

				if (curNetworkView == null) {
					System.out.println("#############NO VIEW!");
					leaf.setView(false);
				} else {
					leaf.setView(true);

					Iterator it = targetNetwork.nodesIterator();
					ViewableNodes vn = factory.createViewableNodes();
					while (it.hasNext()) {
						String viewableID = ((CyNode) it.next())
								.getIdentifier();
						Node viewableNode = factory.createNode();
						viewableNode.setId(viewableID);
						vn.getNode().add(viewableNode);
					}
					leaf.setViewableNodes(vn);

					// Add selected & hidden nodes/edges here!!
					SelectedNodes sn = (SelectedNodes) getSelectedObjects(NODE,
							targetNetwork);

					if (sn != null) {
						leaf.setSelectedNodes(sn);
					}
					SelectedEdges se = (SelectedEdges) getSelectedObjects(EDGE,
							targetNetwork);

					if (se != null) {
						leaf.setSelectedEdges(se);
					}

					HiddenNodes hn = (HiddenNodes) getHiddenObjects(NODE,
							curNetworkView);
					HiddenEdges he = (HiddenEdges) getHiddenObjects(EDGE,
							curNetworkView);
					if (hn != null) {
						leaf.setHiddenNodes(hn);
					}
					if (he != null) {
						leaf.setHiddenEdges(he);
					}
				}

				netList.add(leaf);

			} else {
				walkTree(child);
			}
		}

		//
		// Add hidden/selected nodes and edges
		//

		// Extract selected node information
		String targetID = (String) networkMap.get(node.getUserObject()
				.toString());
		CyNetwork targetNetwork = Cytoscape.getNetwork(targetID);

		if (curNode.getId() != "Network Root") {
			Iterator it = targetNetwork.nodesIterator();
			ViewableNodes vn = factory.createViewableNodes();
			while (it.hasNext()) {

				String viewableID = ((CyNode) it.next()).getIdentifier();
				Node viewableNode = factory.createNode();
				viewableNode.setId(viewableID);
				vn.getNode().add(viewableNode);

			}
			curNode.setViewableNodes(vn);
		}

		// Extract selected nodes and edges
		SelectedNodes sn = (SelectedNodes) getSelectedObjects(NODE,
				targetNetwork);
		if (sn != null) {
			curNode.setSelectedNodes(sn);
		}

		SelectedEdges se = (SelectedEdges) getSelectedObjects(EDGE,
				targetNetwork);
		if (se != null) {
			curNode.setSelectedEdges(se);
		}

		// Extract hidden nodes and edges
		CyNetworkView curNetworkView = Cytoscape.getNetworkView(targetID);
		HiddenNodes hn = (HiddenNodes) getHiddenObjects(NODE, curNetworkView);
		HiddenEdges he = (HiddenEdges) getHiddenObjects(EDGE, curNetworkView);
		if (hn != null) {

			System.out.println("!!!HN is not null");

			curNode.setHiddenNodes(hn);
		}
		if (he != null) {
			System.out.println("!!!HE is not null");
			curNode.setHiddenEdges(he);
		}

		// Add current network to the list.
		netList.add(curNode);
	}

	private Object getHiddenObjects(int type, CyNetworkView view)
			throws JAXBException {

		// List-up all hidden nodes
		if (type == NODE) {
			HiddenNodes hn = factory.createHiddenNodes();
			List hNodeList = hn.getNode();

			CyNode targetNode = null;
			String curNodeName = null;

			for (Iterator i = view.getNodeViewsIterator(); i.hasNext();) {
				NodeView nview = (NodeView) i.next();

				// Check if the node is hidden or not.
				// If it's hidden, store in the session file.
				if (view.showGraphObject(nview)) {
					targetNode = (CyNode) nview.getNode();
					curNodeName = targetNode.getIdentifier();
					Node tempNode = factory.createNode();
					tempNode.setId(curNodeName);

					hNodeList.add(tempNode);
					System.out
							.println("hidden node found: " + tempNode.getId());

					// Keep them hidden...
					view.hideGraphObject(nview);
				}
			}

			// System.out.println("Total hidden == " + hn.getNode().size());
			if (hn.getNode().size() != 0) {
				return hn;
			} else {
				return null;
			}

		} else if (type == EDGE) {
			HiddenEdges he = factory.createHiddenEdges();
			List hEdgeList = he.getEdge();

			CyEdge targetEdge = null;
			String curEdgeName = null;

			for (Iterator i = view.getEdgeViewsIterator(); i.hasNext();) {
				EdgeView eview = (EdgeView) i.next();

				// Check if the edge is hidden or not.
				// If it's hidden, store in the session file.
				if (view.showGraphObject(eview)) {
					targetEdge = (CyEdge) eview.getEdge();
					curEdgeName = targetEdge.getIdentifier();
					cytoscape.generated.Edge tempEdge = factory.createEdge();
					tempEdge.setId(curEdgeName);

					hEdgeList.add(tempEdge);
					System.out
							.println("hidden edge found: " + tempEdge.getId());

					// Keep them hidden...
					view.hideGraphObject(eview);
				}
			}

			if (he.getEdge().size() != 0) {
				return he;
			} else {
				return null;
			}
		}

		return null;
	}

	//
	// List all selected nodes and edges in the session file.
	//
	private Object getSelectedObjects(int type, CyNetwork curNet)
			throws JAXBException {

		if (type == NODE) {

			SelectedNodes sn = factory.createSelectedNodes();
			List sNodeList = sn.getNode();

			Set flaggedNodes = curNet.getFlaggedNodes();

			if (flaggedNodes.size() != 0) {
				Iterator iterator = flaggedNodes.iterator();
				CyNode targetNode = null;
				while (iterator.hasNext()) {
					targetNode = (CyNode) iterator.next();
					String curNodeName = targetNode.getIdentifier();
					Node tempNode = factory.createNode();
					tempNode.setId(curNodeName);

					sNodeList.add(tempNode);
					// System.out.println("++++++Selected node ID is "
					// + curNodeName);
				}

				return sn;
			} else {
				return null;
			}

		} else if (type == EDGE) {
			SelectedEdges se = factory.createSelectedEdges();
			List sEdgeList = se.getEdge();

			Set flaggedEdges = curNet.getFlaggedEdges();

			if (flaggedEdges.size() != 0) {
				Iterator iterator = flaggedEdges.iterator();
				CyEdge targetEdge = null;
				while (iterator.hasNext()) {
					targetEdge = (CyEdge) iterator.next();
					String curEdgeName = targetEdge.getIdentifier();
					cytoscape.generated.Edge tempEdge = factory.createEdge();
					tempEdge.setId(curEdgeName);

					sEdgeList.add(tempEdge);
					// System.out.println("======edge ID is " + curEdgeName);
				}

				return se;
			} else {
				return null;
			}
		}

		return null;
	}

	//
	// Extract states of the 3 Cytopanels.
	//
	// Note:
	// We will store the states of plugins near future.
	// The location of those states will be stored here.
	//
	private Cytopanels getCytoPanelStates() throws JAXBException {
		Cytopanels cps = factory.createCytopanels();
		List cytoPanelList = cps.getCytopanel();

		String[] cytopanelStates = new String[CYTOPANEL_COUNT + 1];
		int[] selectedPanels = new int[CYTOPANEL_COUNT + 1];
		// Extract states of 3 panels.

		cytopanelStates[1] = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.WEST).getState().toString();
		selectedPanels[1] = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.WEST).getSelectedIndex();

		cytopanelStates[2] = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.SOUTH).getState().toString();
		selectedPanels[2] = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.SOUTH).getSelectedIndex();

		cytopanelStates[3] = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.EAST).getState().toString();
		selectedPanels[3] = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.EAST).getSelectedIndex();

		for (int i = 1; i < CYTOPANEL_COUNT + 1; i++) {

			Panels internalPanels = factory.createPanels();
			List iPanelList = internalPanels.getPanel();
			Panel iPanel = factory.createPanel();
			iPanel.setId("test");

			iPanelList.add(iPanel);

			Cytopanel curCp = factory.createCytopanel();
			curCp.setId("CytoPanel" + i);
			curCp.setPanelState(cytopanelStates[i]);
			curCp.setSelectedPanel(Integer.toString(selectedPanels[i]));
			curCp.setPanels(internalPanels);
			cytoPanelList.add(curCp);
		}

		return cps;
	}

}