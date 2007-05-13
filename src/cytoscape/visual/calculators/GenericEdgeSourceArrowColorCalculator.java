package cytoscape.visual.calculators;

import static cytoscape.visual.VisualPropertyType.EDGE_SRCARROW_COLOR;

import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.EdgeAppearance;
import cytoscape.CyNetwork;

import giny.model.Edge;

import java.awt.Color;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class GenericEdgeSourceArrowColorCalculator extends EdgeCalculator {
    /**
     * Creates a new GenericEdgeSourceArrowColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    public GenericEdgeSourceArrowColorCalculator(String name, ObjectMapping m) {
        super(name, m, Color.class, EDGE_SRCARROW_COLOR);
    }

    /**
     * Creates a new GenericEdgeSourceArrowColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    public GenericEdgeSourceArrowColorCalculator(String name, Properties props,
        String baseKey) {
		super(name, props, baseKey, new ColorParser(), Color.black, EDGE_SRCARROW_COLOR);
    }

    /**
     * DOCUMENT ME!
     *
     * @param appr DOCUMENT ME!
     * @param edge DOCUMENT ME!
     * @param network DOCUMENT ME!
     */
    public void apply(EdgeAppearance appr, Edge edge, CyNetwork network) {
        Color c = (Color) getRangeValue(edge);

        // default has already been set - no need to do anything
        if (c == null)
            return;

		appr.setSourceArrowColor(c);
    }
}
