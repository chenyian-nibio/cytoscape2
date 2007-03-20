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
package cytoscape.visual.ui.icon;

import cytoscape.render.immed.GraphGraphics;

import cytoscape.visual.ArrowShape;
import cytoscape.visual.LineTypeDef;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;

import ding.view.DGraphView;

import java.awt.Shape;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;


/**
 * Factory class to create icon sets.
 *
 * @version 0.5
 * @since Cytoscape 2.5
 * @author kono
 */
public class VisualPropertyIconFactory {
	private static Map<NodeShape, Icon> nodeShapeIcons;
	private static Map<ArrowShape, Icon> arrowShapeIcons;
	private static Map<LineTypeDef, Icon> lineTypeIcons;
	private static final int DEF_ICON_SIZE = 32;

	static {
		nodeShapeIcons = new HashMap<NodeShape, Icon>();
		arrowShapeIcons = new HashMap<ArrowShape, Icon>();
		lineTypeIcons = new HashMap<LineTypeDef, Icon>();
		buildIcons();
	}

	private VisualPropertyIconFactory() {
	}

	/**
	 * Get set of icons for the given visual property type.
	 *
	 * @param type
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public static Map getIconSet(final VisualPropertyType type) {
		switch (type) {
			case NODE_SHAPE:
				return nodeShapeIcons;

			case EDGE_SRCARROW_SHAPE:
			case EDGE_TGTARROW_SHAPE:
				return arrowShapeIcons;

			case NODE_LINETYPE:
			case EDGE_LINETYPE:
				return lineTypeIcons;

			default:
		}

		return null;
	}

	private static void buildIcons() {
		final Map<Byte, Shape> nodeShapes = DGraphView.getNodeShapes();
		final Map<Byte, Shape> arrowShapes = DGraphView.getArrowShapes();
		final Map lineShapes = GraphGraphics.getNodeShapes();

		String name;
		VisualPropertyIcon icon;

		/*
		 * Build node shape icons
		 */
		NodeShape shapeType;

		for (Byte key : nodeShapes.keySet()) {
			shapeType = NodeShape.getNodeShape(key);
			name = shapeType.getShapeName();
			icon = new NodeShapeIcon(nodeShapes.get(key), DEF_ICON_SIZE, DEF_ICON_SIZE, name);
			nodeShapeIcons.put(shapeType, icon);
		}

		/*
		 * Build arrow shape icons
		 */

		// First, need to create icon for no arrow head
		icon = new ArrowIcon(null, DEF_ICON_SIZE * 3, DEF_ICON_SIZE, ArrowShape.NONE.getName());
		arrowShapeIcons.put(ArrowShape.NONE, icon);

		ArrowShape arrowShapeType;

		for (Byte key : arrowShapes.keySet()) {
			arrowShapeType = ArrowShape.getArrowShape(key);
			name = arrowShapeType.getName();
			icon = new ArrowIcon(arrowShapes.get(key), DEF_ICON_SIZE * 3, DEF_ICON_SIZE, name);
			arrowShapeIcons.put(arrowShapeType, icon);
		}

		/*
		 * Line icons (not implemented yet)
		 */
	}
}