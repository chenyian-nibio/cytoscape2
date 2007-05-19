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
package cytoscape.visual.ui.editors.continuous;

import cytoscape.Cytoscape;

import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.continuous.ContinuousMappingPoint;

import cytoscape.visual.ui.icon.VisualPropertyIcon;

import org.jdesktop.swingx.JXMultiThumbSlider;
import org.jdesktop.swingx.multislider.Thumb;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class DiscreteTrackRenderer extends JComponent implements VizMapperTrackRenderer {
	/*
	 * Constants for diagram.
	 */
	private static final int ICON_SIZE = VisualPropertyIcon.DEFAULT_ICON_SIZE;
	private static int TRACK_HEIGHT = 70;
	private static final int THUMB_WIDTH = 12;
	private final Font smallFont = new Font("SansSerif", Font.BOLD, 12);
	private final Font largeFont = new Font("SansSerif", Font.BOLD, 18);
	private static final int V_PADDING = 20;
	private static int ARROW_BAR_Y_POSITION = TRACK_HEIGHT + 50;
	private static final String TITLE1 = "Mapping: ";

	/*
	 * Define Colors used in this diagram.
	 */
	private static final Color BORDER_COLOR = Color.DARK_GRAY;

	// private static final int stringPosition = TRACK_HEIGHT + 20;
	private double valueRange;
	private double minValue;
	private double maxValue;
	private Object below;
	private Object above;
	private VisualPropertyType type;
	private String title;

	// Mainly for Icons
	private List<Object> rangeObjects;
	private Object lastObject;

	// HTML document fot tooltip text.
	private List<String> rangeTooltips;
	private JXMultiThumbSlider slider;

	/**
	 * Creates a new DiscreteTrackRenderer object.
	 *
	 * @param type  DOCUMENT ME!
	 * @param minValue  DOCUMENT ME!
	 * @param maxValue  DOCUMENT ME!
	 * @param below  DOCUMENT ME!
	 * @param above  DOCUMENT ME!
	 */
	public DiscreteTrackRenderer(VisualPropertyType type, double minValue, double maxValue,
	                             Object below, Object above) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.below = below;
		this.above = above;

		this.type = type;

		if (type.isNodeProp())
			title = Cytoscape.getVisualMappingManager().getVisualStyle()
			                 .getNodeAppearanceCalculator().getCalculator(type).getMapping(0)
			                 .getControllingAttributeName();
		else
			title = Cytoscape.getVisualMappingManager().getVisualStyle()
			                 .getEdgeAppearanceCalculator().getCalculator(type).getMapping(0)
			                 .getControllingAttributeName();

		valueRange = Math.abs(maxValue - minValue);

		this.setBackground(Color.white);
		this.setForeground(Color.white);
	}

	/**
	 * Creates a new DiscreteTrackRenderer object.
	 *
	 * @param minValue DOCUMENT ME!
	 * @param maxValue DOCUMENT ME!
	 * @param lastRegionObject DOCUMENT ME!
	 * @param cm DOCUMENT ME!
	 */
	public DiscreteTrackRenderer(double minValue, double maxValue, Object lastRegionObject,
	                             ContinuousMapping cm) {
		rangeObjects = new ArrayList<Object>();
		rangeTooltips = new ArrayList<String>();

		this.lastObject = lastRegionObject;
		this.minValue = minValue;
		this.maxValue = maxValue;

		valueRange = Math.abs(maxValue - minValue);

		this.setBackground(Color.white);
		this.setForeground(Color.white);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param g DOCUMENT ME!
	 */
	public void paint(Graphics g) {
		super.paint(g);
		paintComponent(g);
	}

	protected void paintComponent(Graphics gfx) {
		TRACK_HEIGHT = slider.getHeight() - 100;
		ARROW_BAR_Y_POSITION = TRACK_HEIGHT + 50;

		// Turn AA on
		Graphics2D g = (Graphics2D) gfx;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int track_width = slider.getWidth() - THUMB_WIDTH;

		g.translate(THUMB_WIDTH / 2, 12);

		//		 get the list of tumbs
		List<Thumb> stops = slider.getModel().getSortedThumbs();

		int numPoints = stops.size();

		// set up the data for the gradient
		float[] fractions = new float[numPoints];
		Object[] objectValues = new Object[numPoints];

		/*
		 * Find min, max, and ranges
		 */
		int i = 0;

		for (Thumb thumb : stops) {
			objectValues[i] = thumb.getObject();
			fractions[i] = thumb.getPosition();
			i++;
		}

		/*
		 * Draw arrow bar
		 */
		g.setStroke(new BasicStroke(1.0f));
		g.setColor(Color.black);
		g.drawLine(0, ARROW_BAR_Y_POSITION, track_width, ARROW_BAR_Y_POSITION);

		Polygon arrow = new Polygon();
		arrow.addPoint(track_width, ARROW_BAR_Y_POSITION);
		arrow.addPoint(track_width - 20, ARROW_BAR_Y_POSITION - 8);
		arrow.addPoint(track_width - 20, ARROW_BAR_Y_POSITION);
		g.fill(arrow);

		g.setColor(Color.gray);
		g.drawLine(0, ARROW_BAR_Y_POSITION, 15, ARROW_BAR_Y_POSITION - 30);
		g.drawLine(15, ARROW_BAR_Y_POSITION - 30, 25, ARROW_BAR_Y_POSITION - 30);

		g.setFont(smallFont);
		g.drawString("Min=" + minValue, 28, ARROW_BAR_Y_POSITION - 25);

		g.drawLine(track_width, ARROW_BAR_Y_POSITION, track_width - 15, ARROW_BAR_Y_POSITION + 30);
		g.drawLine(track_width - 15, ARROW_BAR_Y_POSITION + 30, track_width - 25,
		           ARROW_BAR_Y_POSITION + 30);

		final String maxStr = "Max=" + maxValue;
		int strWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), maxStr);
		g.drawString(maxStr, track_width - strWidth - 26, ARROW_BAR_Y_POSITION + 35);

		g.setFont(smallFont);
		g.setColor(Color.black);
		strWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), title);
		g.drawString(title, (track_width / 2) - (strWidth / 2), ARROW_BAR_Y_POSITION + 35);

		/*
		 * If no points, just draw empty box.
		 */
		if (numPoints == 0) {
			g.setColor(BORDER_COLOR);
			g.setStroke(new BasicStroke(1.5f));
			g.drawRect(0, 5, track_width, TRACK_HEIGHT);

			return;
		}

		g.setStroke(new BasicStroke(1.0f));

		/*
		 * Fill background
		 */
		g.setColor(Color.white);
		g.fillRect(0, 5, track_width, TRACK_HEIGHT);

		rangeObjects = this.buildIconArray(stops.size() + 1);

		g.setStroke(new BasicStroke(1.0f));

		int newX = 0;

		Point2D p1 = new Point2D.Float(0, 5);
		Point2D p2 = new Point2D.Float(0, 5);

		int iconLocX;
		int iconLocY;

		/*
		 * Draw separators and icons
		 */
		for (i = 0; i < stops.size(); i++) {
			newX = (int) (track_width * (fractions[i] / 100));

			p2.setLocation(newX, 5);
			g.setColor(Color.black);
			g.setStroke(new BasicStroke(1.0f));

			g.drawLine(newX, 5, newX, TRACK_HEIGHT + 4);

			g.setColor(Color.DARK_GRAY);
			g.setFont(new Font("SansSerif", Font.BOLD, 10));

			Float curPositionValue = ((Double) (((fractions[i] / 100) * valueRange)
			                         - Math.abs(minValue))).floatValue();
			String valueString = String.format("%10.5f", curPositionValue);

			int flipLimit = 90;
			int borderVal = track_width - newX;

			if (((i % 2) == 0) && (flipLimit < borderVal)) {
				g.drawLine(newX, ARROW_BAR_Y_POSITION, newX + 20, ARROW_BAR_Y_POSITION - 15);
				g.drawLine(newX + 20, ARROW_BAR_Y_POSITION - 15, newX + 30,
				           ARROW_BAR_Y_POSITION - 15);
				g.setColor(Color.black);
				g.drawString(valueString, newX + 33, ARROW_BAR_Y_POSITION - 11);
			} else if (((i % 2) == 1) && (flipLimit < borderVal)) {
				g.drawLine(newX, ARROW_BAR_Y_POSITION, newX + 20, ARROW_BAR_Y_POSITION + 15);
				g.drawLine(newX + 20, ARROW_BAR_Y_POSITION + 15, newX + 30,
				           ARROW_BAR_Y_POSITION + 15);
				g.setColor(Color.black);
				g.drawString(valueString, newX + 33, ARROW_BAR_Y_POSITION + 19);
			} else if (((i % 2) == 0) && (flipLimit >= borderVal)) {
				g.drawLine(newX, ARROW_BAR_Y_POSITION, newX - 20, ARROW_BAR_Y_POSITION - 15);
				g.drawLine(newX - 20, ARROW_BAR_Y_POSITION - 15, newX - 30,
				           ARROW_BAR_Y_POSITION - 15);
				g.setColor(Color.black);
				g.drawString(valueString, newX - 90, ARROW_BAR_Y_POSITION - 11);
			} else {
				g.drawLine(newX, ARROW_BAR_Y_POSITION, newX - 20, ARROW_BAR_Y_POSITION + 15);
				g.drawLine(newX - 20, ARROW_BAR_Y_POSITION + 15, newX - 30,
				           ARROW_BAR_Y_POSITION + 15);
				g.setColor(Color.black);
				g.drawString(valueString, newX - 90, ARROW_BAR_Y_POSITION + 19);
			}

			g.setColor(Color.black);
			g.fillOval(newX - 3, ARROW_BAR_Y_POSITION - 3, 6, 6);

			iconLocX = newX - (((newX - (int) p1.getX()) / 2) + (ICON_SIZE / 2));
			iconLocY = ((TRACK_HEIGHT) / 2) - (ICON_SIZE / 2) + 5;

			//			if (ICON_SIZE < (newX - p1.getX()))
			//				g.drawImage(((ImageIcon) rangeObjects.get(i)).getImage(), iconLocX, iconLocY, this);
			if (i == 0) {
				drawIcon(below, g, iconLocX, iconLocY);

				//				g.drawString(below.toString(), iconLocX, iconLocY);
			} else {
				drawIcon(objectValues[i], g, iconLocX, iconLocY);

				//				g.drawString(objectValues[i].toString(), iconLocX, iconLocY);
			}

			p1.setLocation(p2);
		}

		/*
		 * Draw last region (above region)
		 */
		p2.setLocation(track_width, 5);

		iconLocX = track_width - (((track_width - (int) p1.getX()) / 2) + (ICON_SIZE / 2));
		iconLocY = ((TRACK_HEIGHT) / 2) - (ICON_SIZE / 2) + 5;
		//		g.drawImage(((ImageIcon) rangeObjects.get(i)).getImage(), iconLocX, iconLocY, this);
		//		g.drawString(above.toString(), iconLocX, iconLocY);
		drawIcon(above, g, iconLocX, iconLocY);
		/*
		 * Finally, draw border line (rectangle)
		 */
		g.setColor(BORDER_COLOR);
		g.setStroke(new BasicStroke(1.5f));
		g.drawRect(0, 5, track_width, TRACK_HEIGHT);

		g.translate(-THUMB_WIDTH / 2, -12);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param slider DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public JComponent getRendererComponent(JXMultiThumbSlider slider) {
		this.slider = slider;

		return this;
	}

	protected List getRanges() {
		List range = new ArrayList();

		return range;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getToolTipForCurrentLocation(int x, int y) {
		int oldX = 0;
		int newX;

		final List<Thumb> stops = slider.getModel().getSortedThumbs();

		int i = 1;

		for (Thumb thumb : stops) {
			newX = (int) (slider.getWidth() * (thumb.getPosition() / 100));

			if ((oldX <= x) && (x <= newX) && (V_PADDING < y) && (y < (V_PADDING + TRACK_HEIGHT)))
				return "This is region " + i;

			i++;
			oldX = newX + 1;
		}

		if ((oldX <= x) && (x <= slider.getWidth()) && (V_PADDING < y)
		    && (y < (V_PADDING + TRACK_HEIGHT)))
			return "Last Area: " + oldX + " - " + slider.getWidth() + " (x, y) = " + x + ", " + y;

		return null;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Object getObjectInRange(int x, int y) {
		final int range = getRangeID(x, y);

		if (range == -1)
			return null;

		if (range == 0) {
			return null;
		} else if (range == slider.getModel().getThumbCount()) {
		}

		return rangeObjects.get(getRangeID(x, y));
	}

	/*
	 * Get region id.
	 *
	 * +-------------------------------------------
	 * |    0     |      1      |     2     |  ...
	 * +-------------------------------------------
	 *
	 */
	protected int getRangeID(int x, int y) {
		int oldX = 0;
		int newX;

		final List<Thumb> stops = slider.getModel().getSortedThumbs();
		Thumb thumb;
		int i;

		for (i = 0; i < stops.size(); i++) {
			thumb = stops.get(i);
			newX = (int) (slider.getWidth() * (thumb.getPosition() / 100));

			if ((oldX <= x) && (x <= newX) && (V_PADDING < y) && (y < (V_PADDING + TRACK_HEIGHT)))
				return i;

			oldX = newX + 1;
		}

		if ((oldX <= x) && (x <= slider.getWidth()) && (V_PADDING < y)
		    && (y < (V_PADDING + TRACK_HEIGHT)))
			return i;

		// Invalid range
		return -1;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param iconWidth DOCUMENT ME!
	 * @param iconHeight DOCUMENT ME!
	 * @param mapping DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public static ImageIcon getTrackGraphicIcon(int iconWidth, int iconHeight,
	                                            ContinuousMapping mapping) {
		final BufferedImage bi = new BufferedImage(iconWidth, iconHeight, BufferedImage.TYPE_INT_RGB);
		final Graphics2D g2 = bi.createGraphics();

		// Turn Anti-alias on
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		final int leftSpace = 2;
		int trackHeight = iconHeight - 15;
		int trackWidth = iconWidth - leftSpace - 5;

		g2.setBackground(Color.white);

		/*
		 * Draw background
		 */
		g2.setColor(Color.white);
		g2.fillRect(0, 0, iconWidth, iconHeight);
		g2.setStroke(new BasicStroke(1.0f));
		g2.setColor(Color.black);

		/*
		 * Compute fractions from mapping
		 */
		List<ContinuousMappingPoint> points = mapping.getAllPoints();
		final int pointCount = points.size();

		/*
		 * If no points, just return empty rectangle.
		 */
		if (pointCount == 0) {
			g2.drawRect(leftSpace, 0, trackWidth, trackHeight);

			return new ImageIcon(bi);
		}

		float[] fractions = new float[pointCount + 2];
		double[] values = new double[pointCount];

		Object[] objValues = new Object[pointCount + 2];

		objValues[0] = points.get(0).getRange().lesserValue;

		if (pointCount == 1) {
			objValues[1] = points.get(0).getRange().equalValue;
			objValues[2] = points.get(0).getRange().greaterValue;
		} else {
			// "Above" value
			objValues[objValues.length - 1] = points.get(points.size() - 1).getRange().greaterValue;

			for (int i = 0; i < pointCount; i++)
				objValues[i + 1] = points.get(i).getRange().equalValue;
		}

		//List<ImageIcon> iconList = buildIconArray(objValues);
		final Point2D start = new Point2D.Float(10, 0);
		final Point2D end = new Point2D.Float(trackWidth, trackHeight);

		//		int i=1;
		//		
		//		g2.setFont(new Font("SansSerif", Font.BOLD, 9));
		//		int strWidth;
		//		for(ContinuousMappingPoint point: points) {
		//			String p = Double.toString(point.getValue());
		//			g2.setColor(Color.black);
		//			strWidth = SwingUtilities.computeStringWidth(g2.getFontMetrics(), p);
		//			g2.drawString(p, fractions[i]*iconWidth - strWidth/2, iconHeight -7);
		//			i++;
		//		}
		return new ImageIcon(bi);
	}

	private static List buildIconArray(int size) {
		List<ImageIcon> icons = new ArrayList<ImageIcon>();

		Map iconMap = NodeShape.getIconSet();

		Object[] keys = iconMap.keySet().toArray();

		for (int i = 0; i < size; i++)
			icons.add((ImageIcon) iconMap.get(keys[i]));

		return icons;
	}

	private Shape getIcon(Object key) {
		final BufferedImage image = new BufferedImage(40, 40, BufferedImage.TYPE_INT_RGB);

		final Graphics2D gfx = image.createGraphics();
		Map icons = type.getVisualProperty().getIconSet();
		JLabel label = new JLabel();
		label.setIcon((Icon) icons.get(key));
		label.setText("test1");
		gfx.setBackground(Color.white);
		gfx.setColor(Color.red);
		gfx.drawString("Test1", 0, 0);

		//		label.paint(gfx);
		return ((VisualPropertyIcon) icons.get(key)).getShape();
	}

	/*
	 * Draw icon object based on the given data type.
	 */
	private void drawIcon(Object key, Graphics2D g, int x, int y) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.translate(x, y);

		switch (type) {
			case NODE_SHAPE:
			case EDGE_SRCARROW_SHAPE:
			case EDGE_TGTARROW_SHAPE:

				final VisualPropertyIcon icon = (VisualPropertyIcon) type.getVisualProperty()
				                                                         .getIconSet().get(key);
				g.draw(icon.getShape());

				break;

			case NODE_FONT_FACE:
			case EDGE_FONT_FACE:

				final Font font = (Font) key;
				final String fontName = font.getFontName();
				g.setFont(new Font(fontName, font.getStyle(), 40));
				g.drawString("A", 0, 30);

				g.setFont(new Font(fontName, font.getStyle(), 10));

				int stringWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), fontName);
				g.drawString(fontName, 20 - (stringWidth / 2), 43);

				break;

			case NODE_LINETYPE:
			case EDGE_LINETYPE:
				break;

			case NODE_LABEL_POSITION:
				break;

			case NODE_LABEL:
			case NODE_TOOLTIP:
			case EDGE_LABEL:
			case EDGE_TOOLTIP:
				break;

			default:
				break;
		}

		g.translate(-x, -y);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Double getSelectedThumbValue() {
		final float position = slider.getModel().getThumbAt(slider.getSelectedIndex()).getPosition();
		final double thumbVal = (((position / 100) * valueRange) - Math.abs(minValue));

		return thumbVal;
	}
}