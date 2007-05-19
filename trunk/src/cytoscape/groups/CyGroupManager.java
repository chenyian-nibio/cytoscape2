/* vim :set ts=2: */
/*
  File: CyGroup.java

  Copyright (c) 2007, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.groups;

import cytoscape.data.CyAttributes;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;

import giny.model.RootGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * The CyGroup class provides the implementation for a group model that
 * maintains the list of nodes belonging to a group, the parent of a particular
 * group, and the node that represents the group.  Group information is stored
 * in the CyGroup itself, as well as in special group attributes that are associated
 * with the network, nodes, and edges involved.  These attributes provide a natural
 * mechanism for the saving and restoration of groups.  There are also opaque flags
 */
public class CyGroupManager {
	// Static data

	/**
	 * The list of groups, indexed by the CyNode that represents the group.  The values
	 * are the CyGroup itself.
	 */
	private static HashMap<CyNode, CyGroup> groupMap = new HashMap();

	/**
	 * The list of group viewers currently registered.
	 */
	private static HashMap<String, CyGroupViewer> viewerMap = new HashMap();

	/**
	 * The list of groups, indexed by the managing viewer
	 */
	private static HashMap<CyGroupViewer, List<CyGroup>> groupViewerMap = new HashMap();

	// Static methods
	/**
	 * getCyGroup is a static method that returns a CyGroup structure when
	 * given the CyNode that represents this group.
	 *
	 * @param groupNode the CyNode that represents this group
	 * @return the associated CyGroup structure
	 */
	public static CyGroup getCyGroup(CyNode groupNode) {
		if ((groupMap == null) || !groupMap.containsKey(groupNode))
			return null;

		return (CyGroup) groupMap.get(groupNode);
	}

	/**
	 * getGroup is a static method that returns a CyGroup structure when
	 * given a CyNode that is a member of a group.
	 *
	 * @param memberNode a CyNode whose group membership we're looking for
	 * @return a list of CyGroups this node is a member of
	 */
	public static List<CyGroup> getGroup(CyNode memberNode) {
		List<CyGroup> groupList = new ArrayList<CyGroup>();
		Iterator groupIter = groupMap.values().iterator();

		while (groupIter.hasNext()) {
			CyGroup group = (CyGroup) groupIter.next();

			if (group.contains(memberNode))
				groupList.add(group);
		}

		if (groupList.size() == 0)
			return null;

		return groupList;
	}

	/**
	 * Return the list of all groups
	 *
	 * @return the list of groups
	 */
	public static List<CyGroup> getGroupList() {
		Collection<CyGroup> c = groupMap.values();

		return new ArrayList<CyGroup>(c);
	}

	/**
	 * Return the list of all groups managed by a particular viewer
	 *
	 * @param viewer the CyGroupViewer
	 * @return the list of groups
	 */
	public static List<CyGroup> getGroupList(CyGroupViewer viewer) {
		if (!groupViewerMap.containsKey(viewer))
			return null;
		List<CyGroup> groupList = groupViewerMap.get(viewer);

		return groupList;
	}

	/**
	 * Create a new, empty group.  Use this to get a new group.  In particular,
	 * this form should be used by internal routines (as opposed to view
	 * implementations) as this form will cause the viewer to be notified of
	 * the group creation.  Viewers should use createGroup(String, List, String)
	 * as defined below.
	 *
	 * @param groupName the identifier to use for this group -- should be unique!
	 * @param viewer the name of the viewer to manage this group
	 * @return the newly created group
	 */
	public static CyGroup createGroup(String groupName, String viewer) {
		// Create the group
		CyGroup group = new CyGroupImpl(groupName);
		groupMap.put(group.getGroupNode(), group);
		setGroupViewer(group, viewer, true);

		return group;
	}

	/**
	 * Create a new group with a list of nodes as initial members.  Note that this
	 * method is the prefered method to be used by viewers.  Using this method,
	 * once the group is created the viewer is *not* notified (since it is assumed
	 * they are doing the creation).
	 *
	 * @param groupName the identifier to use for this group -- should be unique!
	 * @param nodeList the initial set of nodes for this group
	 * @param viewer the name of the viewer to manage this group
	 */
	public static CyGroup createGroup(String groupName, List nodeList, String viewer) {
		// Create the group
		CyGroup group = new CyGroupImpl(groupName, nodeList);
		groupMap.put(group.getGroupNode(), group);
		setGroupViewer(group, viewer, false);

		return group;
	}

	/**
	 * Create a new group with a list of nodes as initial members, and a precreated
	 * group node.  This is usually used by the XGMML reader since the group node
	 * may need to alread be created with its associated "extra" edges.  Note that
	 * the node will be created, but *not* added to the network.  That is the
	 * responsibility of the appropriate viewer.
	 *
	 * @param groupNode the groupNode to use for this group
	 * @param nodeList the initial set of nodes for this group
	 * @param viewer the name of the viewer to manage this group
	 */
	public static CyGroup createGroup(CyNode groupNode, List nodeList, String viewer) {
		// Create the group
		CyGroup group = new CyGroupImpl(groupNode, nodeList);
		groupMap.put(group.getGroupNode(), group);

		if (viewer != null)
			setGroupViewer(group, viewer, true);

		return group;
	}

	/**
	 * Remove (delete) a group
	 *
	 * @param group the group to remove
	 */
	public static void removeGroup(CyGroup group) {
		CyNode groupNode = group.getGroupNode();
		removeGroup(groupNode);
	}

	/**
	 * Remove (delete) a group
	 *
	 * @param groupNode the group node of the group to remove
	 */
	public static void removeGroup(CyNode groupNode) {
		if (groupMap.containsKey(groupNode)) {
			notifyRemoveGroup((CyGroup) groupMap.get(groupNode));

			// Remove this from the viewer's list
			CyGroup group = groupMap.get(groupNode);
			String viewer = group.getViewer();

			if ((viewer != null) && viewerMap.containsKey(viewer)) {
				CyGroupViewer groupViewer = viewerMap.get(viewer);
				List<CyGroup> gList = groupViewerMap.get(groupViewer);
				gList.remove(group);
			}

			// Remove it from the groupMap
			groupMap.remove(groupNode);

			// Remove this group from all the nodes
			List<CyNode> nodeList = group.getNodes();
			Iterator <CyNode> nIter = nodeList.iterator();
			while (nIter.hasNext()) {
				CyNode node = nIter.next();
				node.removeFromGroup(group);
			}

			CyNetwork network = Cytoscape.getCurrentNetwork();
			network.removeNode(groupNode.getRootGraphIndex(), false);
			// Remove the group node form the network
			// RootGraph rg = groupNode.getRootGraph();
			// Remove it from the root graph
			// rg.removeNode(groupNode);
		}
	}

	/**
	 * See if this CyNode represents a group
	 *
	 * @param groupNode the node we want to test
	 * @return 'true' if groupNode is a group
	 */
	public static boolean isaGroup(CyNode groupNode) {
		return groupMap.containsKey(groupNode);
	}

	// Viewer methods
	/**
	 * Register a viewer.
	 *
	 * @param viewer the viewer we're registering
	 */
	public static void registerGroupViewer(CyGroupViewer viewer) {
		viewerMap.put(viewer.getViewerName(), viewer);
	}

	/**
	 * Set the viewer for a group
	 *
	 * @param group the group we're associating with a viewer
	 * @param viewer the viewer
	 * @param notify if 'true' the viewer will be notified of the creation
	 */
	public static void setGroupViewer(CyGroup group, String viewer, boolean notify) {
		if ((viewer != null) && viewerMap.containsKey(viewer)) {
			// get the viewer
			CyGroupViewer v = (CyGroupViewer) viewerMap.get(viewer);

			// create the list if necessary
			if (!groupViewerMap.containsKey(v))
				groupViewerMap.put(v, new ArrayList<CyGroup>());

			// Add this group to the list
			groupViewerMap.get(v).add(group);

			if (notify)
				v.groupCreated(group);
		}

		((CyGroupImpl)group).setViewer(viewer);
	}

	/**
	 * Return the viewer object for a named viewer
	 *
	 * @param viewerName the name of the viewer
	 * @return the viewer object
	 */
	public static CyGroupViewer getGroupViewer(String viewerName) {
		if ((viewerName != null) && viewerMap.containsKey(viewerName))
			return viewerMap.get(viewerName);
		return null;
	}

	/**
	 * Notify a viewer that a group has been created for them to manage.
	 *
	 * @param group the group that was just created
	 */
	public static void notifyCreateGroup(CyGroup group) {
		String viewer = group.getViewer();

		if ((viewer != null) && viewerMap.containsKey(viewer)) {
			CyGroupViewer v = viewerMap.get(viewer);
			v.groupCreated(group);
		}
	}

	/**
	 * Notify a viewer the a group of interest is going to be removed.
	 *
	 * @param group the group to be removed
	 */
	public static void notifyRemoveGroup(CyGroup group) {
		String viewer = group.getViewer();

		if ((viewer != null) && viewerMap.containsKey(viewer)) {
			CyGroupViewer v = viewerMap.get(viewer);
			v.groupWillBeRemoved(group);
		}
	}

}