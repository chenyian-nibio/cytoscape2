// VisualPropertiesDialog.java
//---------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package cytoscape.dialogs;
//---------------------------------------------------------------------------------------
import javax.swing.*;
import javax.swing.AbstractAction;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import y.view.LineType;
import y.view.Arrow;

import cytoscape.GraphObjAttributes;
import cytoscape.data.*;
import cytoscape.vizmap.*;
import cytoscape.dialogs.NewSlider;
import cytoscape.util.MutableColor;
import cytoscape.util.MutableString;
import cytoscape.util.MutableBool;
import cytoscape.dialogs.GeneralColorDialogListener;
import cytoscape.dialogs.MiscGB;
//--------------------------------------------------------------------------------------
public class VisualPropertiesDialog extends JDialog {

    IconPopupButton shapeDefault;
    IconPopupButton lineTypeDefault;
    IconPopupButton arrowDefault;
    IntegerEntryField sizeDefault;
    AttributeMapper aMapper;
    Frame parentFrame;
    MutableColor nColor;
    MutableColor bColor;
    MutableColor bgColor;
    MutableColor eColor;
    MutableString localNodeLabelKey;
    MutableString parentNodeLabelKey;
    MutableString localEdgeKey;
    MutableBool applied;
    EdgeTextPanel edgeTextPanel;
//--------------------------------------------------------------------------------------
public VisualPropertiesDialog (Frame parentFrame,
			       String title,
			       AttributeMapper mapper,
			       GraphObjAttributes nodeAttribs,
			       GraphObjAttributes edgeAttribs,
			       MutableString nodeLabelKey,
			       MutableBool applied)
{
  super (parentFrame, true);
  setTitle (title);
  this.parentFrame = parentFrame;

  aMapper = mapper;
  nColor = new MutableColor(getBasicColor(VizMapperCategories.NODE_FILL_COLOR));
  bColor = new MutableColor(getBasicColor(VizMapperCategories.NODE_BORDER_COLOR));
  bgColor = new MutableColor(getBasicColor(VizMapperCategories.BG_COLOR));
  eColor = new MutableColor(getBasicColor(VizMapperCategories.EDGE_COLOR));
  localNodeLabelKey = new MutableString(nodeLabelKey.getString());
  parentNodeLabelKey = nodeLabelKey;
  this.applied = applied;

  GridBagGroup gbg = new GridBagGroup();
  
  JPanel mainPanel = gbg.panel;
  MiscGB.pad(gbg.constraints,5,5);

  GridBagGroup defGBG = new GridBagGroup("Defaults");
  MiscGB.pad(defGBG.constraints,5,5);

  int yPos=0;
  int yDef=0;

  JButton colorButton = new JButton("Node Color");
  JLabel colorLabel = MiscGB.createColorLabel(nColor.getColor());
  colorButton.addActionListener(new
      GeneralColorDialogListener(this,nColor,colorLabel,
  				 "Choose a Node Color"));
  MiscGB.insert(defGBG,colorButton,0,yDef,1,1,GridBagConstraints.HORIZONTAL);
  MiscGB.insert(defGBG,colorLabel,1,yDef);
  yDef++;

  JButton borderColorButton = new JButton("Node Border Color");
  JLabel borderColorLabel = MiscGB.createColorLabel(bColor.getColor());
  borderColorButton.addActionListener(new
      GeneralColorDialogListener(this,bColor,borderColorLabel,
  				 "Choose a Node Border Color"));
  MiscGB.insert(defGBG,borderColorLabel,1,yDef);
  MiscGB.insert(defGBG,borderColorButton,0,yDef,1,1,GridBagConstraints.HORIZONTAL);
  yDef++;

  JButton bgColorButton
      = new JButton("Background Color");
  JLabel bgColorLabel = MiscGB.createColorLabel(bgColor.getColor());
  bgColorButton.addActionListener(new
      GeneralColorDialogListener(this,bgColor,bgColorLabel,
  				 "Choose a Background Color"));
  MiscGB.insert(defGBG,bgColorLabel,1,yDef);
  MiscGB.insert(defGBG,bgColorButton,0,yDef,1,1,GridBagConstraints.HORIZONTAL);
  yDef++;

  sizeDefault = 
      new IntegerEntryField
	  ("Node Size",
	   ((Integer)aMapper.getDefaultValue(VizMapperCategories.NODE_HEIGHT)).intValue(),
	   500);
  MiscGB.insert(defGBG,sizeDefault.getLabel(),0,yDef);
  MiscGB.insert(defGBG,sizeDefault.getField(),1,yDef);
  yDef++;

  initializeShapeDefault();
  MiscGB.insert(defGBG,shapeDefault.getButton(),0,yDef,1,1,GridBagConstraints.HORIZONTAL);
  MiscGB.insert(defGBG,shapeDefault.getLabel(),1,yDef);
  yDef++;

  initializeLineTypeDefault();
  MiscGB.insert(defGBG,lineTypeDefault.getButton(),0,yDef,1,1,GridBagConstraints.HORIZONTAL);
  MiscGB.insert(defGBG,lineTypeDefault.getLabel(),1,yDef);
  yDef++;

  initializeArrowDefault();
  MiscGB.insert(defGBG,arrowDefault.getButton(),0,yDef,1,1,GridBagConstraints.HORIZONTAL);
  MiscGB.insert(defGBG,arrowDefault.getLabel(),1,yDef);
  yDef++;

  //////////////////////////////////////////////
  MiscGB.insert(gbg,defGBG.panel,0,yPos,2,1,GridBagConstraints.HORIZONTAL);
  yPos++;

  //////////////////////////////////////////////
  GridBagGroup labelGBG = new GridBagGroup("Node Label Mapping");
  JPanel labelTextPanel
      = new LabelTextPanel(nodeAttribs,localNodeLabelKey);
  MiscGB.insert(labelGBG,labelTextPanel,0,0);
  MiscGB.insert(gbg,labelGBG.panel,0,yPos,2,1,GridBagConstraints.HORIZONTAL);
  yPos++;

  //////////////////////////////////////////////
  GridBagGroup edgeGBG = new GridBagGroup("Edge Color Mapping");
  if(localEdgeKey==null) localEdgeKey = new MutableString("temp");
  edgeTextPanel
      = new EdgeTextPanel(edgeAttribs,aMapper,parentFrame,localEdgeKey);
  MiscGB.insert(edgeGBG,edgeTextPanel,0,0);
  MiscGB.insert(gbg,edgeGBG.panel,0,yPos,2,1,GridBagConstraints.HORIZONTAL);
  yPos++;

  JButton applyButton = new JButton ("Apply");
  applyButton.addActionListener (new ApplyAction ());
  MiscGB.insert(gbg,applyButton,0,yPos);
  JButton cancelButton = new JButton ("Cancel");
  cancelButton.addActionListener (new CancelAction ());
  MiscGB.insert(gbg,cancelButton,1,yPos);
  yPos++;

  setContentPane (mainPanel);
} // PopupDialog ctor

public class ApplyAction extends AbstractAction {
  ApplyAction () {
      super ("");
  }

    public void actionPerformed (ActionEvent e) {
	
	Object o;
	
	Color node = nColor.getColor();
	if(node != null)
	    o = aMapper.setDefaultValue(VizMapperCategories.NODE_FILL_COLOR, node);
	Color bg = bgColor.getColor();
	if(bg != null)
	    o = aMapper.setDefaultValue(VizMapperCategories.BG_COLOR, bg);
	Byte shape = (Byte)shapeDefault.getIconObject();
	if(shape != null)
	    o = aMapper.setDefaultValue(VizMapperCategories.NODE_SHAPE, shape);
	LineType line = (LineType)lineTypeDefault.getIconObject();
	if(line != null)
	    o = aMapper.setDefaultValue(VizMapperCategories.EDGE_LINETYPE, line);
	Integer size = sizeDefault.getInteger();
	if(size != null) {
	    o = aMapper.setDefaultValue(VizMapperCategories.NODE_HEIGHT, size);
	    o = aMapper.setDefaultValue(VizMapperCategories.NODE_WIDTH, size);
	}
	Color border = bColor.getColor();
	if(border != null)
	    o = aMapper.setDefaultValue(VizMapperCategories.NODE_BORDER_COLOR, border);
	Arrow arrow = (Arrow)arrowDefault.getIconObject();
	if(arrow != null)
	    o = aMapper.setDefaultValue(VizMapperCategories.EDGE_TARGET_DECORATION, arrow);
	
      /*
      EdgeArrowColor.removeThenAddEdgeColor(aMapper,"pp",ppColor.getColor());
      EdgeArrowColor.removeThenAddEdgeColor(aMapper,"pd",pdColor.getColor());
      */

      if(edgeTextPanel.getWhetherToUseTheMap()) {
	  Map m = edgeTextPanel.getMap();
	  if(m != null) {
	      aMapper.setAttributeMapEntry(VizMapperCategories.EDGE_COLOR,
					   localEdgeKey.getString(),
					   new DiscreteMapper(m));

	  }
      }
      parentNodeLabelKey.setString(localNodeLabelKey.getString());
      applied.setBool(true);
      VisualPropertiesDialog.this.dispose ();
  }

} // ApplyAction

public class CancelAction extends AbstractAction {
  CancelAction () { super (""); }
  public void actionPerformed (ActionEvent e) {
      applied.setBool(false);
      VisualPropertiesDialog.this.dispose ();
  }
} // CancelAction

    private Color getBasicColor(Integer category) {
	Color tempColor;
	try {
	    tempColor = 
		(Color)aMapper.getDefaultValue(category);
	} catch (NullPointerException ex) {
	    tempColor = new Color(255,255,255);
	}
	if(tempColor != null)
	    return tempColor;
	else {
	    return new Color(255,255,255);
	}
    }

    private Color getDMColor(Integer category, String key) {
	Color tempColor;

	DiscreteMapper dm =
	    (DiscreteMapper)
	    aMapper.getValueMapper(category);

	Map valueMap = dm.getValueMap();
	tempColor = (Color)valueMap.get(key);

	if(tempColor != null)
	    return tempColor;
	else {
	    return new Color(255,255,255);
	}
    }

    /**
     *  For each icon-list of attributes, there is an IconPopupButton,
     *  initialized through a function called
     *     initialize<Whatever>Default()
     *  (several instances of this sort of function follow this comment).
     *
     *  Construction of an IconPopupButton requires a title for the button,
     *  a name of the attribute in question, two hashes for going from
     *  Object to String and back again, a list of ImageIcon's, a current
     *  Object that is selected, and a reference to this, the parent dialog.
     *
     *  Each different icon-list of attributes also depends on a few
     *  functions from MiscDialog.java.  These functions create the
     *  HashMaps and the list of ImageIcons.
     *
     *  Finally, the value in each of these IconPopupButton's is taken and
     *  used during ApplyAction's actionPerformed(), above.
     *
     */

    private void initializeArrowDefault() {
	int ns = ((Integer)aMapper.getDefaultValue(VizMapperCategories.NODE_HEIGHT)).intValue();
	Object currentArrow =  aMapper.getDefaultValue(VizMapperCategories.EDGE_TARGET_DECORATION);
	HashMap arrowToString = MiscDialog.getArrowToStringHashMap(ns);
	HashMap stringToArrow = MiscDialog.getStringToArrowHashMap(ns);
	ImageIcon [] icons = MiscDialog.getArrowIcons();
	arrowDefault =
	    new IconPopupButton ("Arrow",
				 "Arrow",
				 arrowToString,
				 stringToArrow,
				 icons,
				 currentArrow,
				 this);
    }


    private void initializeShapeDefault() {
	Object currentShape =  aMapper.getDefaultValue(VizMapperCategories.NODE_SHAPE);
	HashMap shapeToString = MiscDialog.getShapeByteToStringHashMap();
	HashMap stringToShape = MiscDialog.getStringToShapeByteHashMap();
	ImageIcon [] icons = MiscDialog.getShapeIcons();
	shapeDefault =
	    new IconPopupButton ("Node Shape",
				 "Node Shape",
				 shapeToString,
				 stringToShape,
				 icons,
				 currentShape,
				 this);
    }


    private void initializeLineTypeDefault() {
	Object currentLineType =  aMapper.getDefaultValue(VizMapperCategories.EDGE_LINETYPE);
	HashMap lineTypeToString = MiscDialog.getLineTypeToStringHashMap();
	HashMap stringToLineType = MiscDialog.getStringToLineTypeHashMap();
	ImageIcon [] icons = MiscDialog.getLineTypeIcons();
	lineTypeDefault =
	    new IconPopupButton ("Line Type",
				 "Line Type",
				 lineTypeToString,
				 stringToLineType,
				 icons,
				 currentLineType,
				 this);
    }


} // class VisualPropertiesDialog
