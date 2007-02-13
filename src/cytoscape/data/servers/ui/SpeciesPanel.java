/*
 * SpeciesPanel.java
 *
 * Created on 2006/04/13, 16:08
 */
package cytoscape.data.servers.ui;

import cytoscape.CytoscapeInit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URL;

import java.util.StringTokenizer;

import javax.swing.JPanel;


/**
 *
 * @author kono
 */
public class SpeciesPanel extends JPanel {
	// Resource Location
	private static final String TAXON_RESOURCE_FILE = "/cytoscape/resources/tax_report.txt";
	private String currentSpeciesName;

	/** Creates new form SpeciesPanel */
	public SpeciesPanel() {
		currentSpeciesName = CytoscapeInit.getProperties().getProperty("defaultSpeciesName");
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */

	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		jLabel1 = new javax.swing.JLabel();
		jSeparator1 = new javax.swing.JSeparator();
		overwriteCheckBox = new javax.swing.JCheckBox();
		overwriteComboBox = new javax.swing.JComboBox();
		messageScrollPane = new javax.swing.JScrollPane();
		messageEditorPane = new javax.swing.JEditorPane();

		setTaxonomyTable();
		overwriteComboBox.setEditable(false);
		overwriteComboBox.setEnabled(false);
		overwriteComboBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					overwriteComboBoxActionPerformed(evt);
				}
			});

		jLabel1.setFont(new java.awt.Font("Dialog", 1, 14));
		jLabel1.setText("Default Species Name");

		overwriteCheckBox.setFont(new java.awt.Font("Dialog", 1, 10));
		overwriteCheckBox.setText("Overwrite default species name with:");
		overwriteCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		overwriteCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		overwriteCheckBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					overwriteCheckBoxActionPerformed(evt);
				}
			});

		messageScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		messageEditorPane.setEditable(false);
		messageEditorPane.setContentType("text/html");
		messageEditorPane.setText("<html><body><strong><u>Note</u></strong><br>"
		                          + "The property <strong><i>defaultSpeciesName</i></strong>"
		                          + " will be used <br>only when a node does not have an attribute <strong><i>species</i></strong>.<br><br>"
		                          + "If you select a species name from this list, the value<br>"
		                          + "will be used as the <strong><i>defaultSpeciesname</i></strong>."
		                          + "</body></html>");

		messageScrollPane.setViewportView(messageEditorPane);

		// Layout
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                .add(layout.createSequentialGroup().addContainerGap()
		                                           .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                      .add(layout.createSequentialGroup()
		                                                                 .add(overwriteCheckBox,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      338, Short.MAX_VALUE)
		                                                                 .add(36, 36, 36))
		                                                      .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                                           layout.createSequentialGroup()
		                                                                 .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
		                                                                            .add(org.jdesktop.layout.GroupLayout.LEADING,
		                                                                                 jSeparator1,
		                                                                                 org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                 362,
		                                                                                 Short.MAX_VALUE)
		                                                                            .add(org.jdesktop.layout.GroupLayout.LEADING,
		                                                                                 overwriteComboBox,
		                                                                                 0, 362,
		                                                                                 Short.MAX_VALUE)
		                                                                            .add(org.jdesktop.layout.GroupLayout.LEADING,
		                                                                                 messageScrollPane,
		                                                                                 org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                 362,
		                                                                                 Short.MAX_VALUE)
		                                                                            .add(jLabel1,
		                                                                                 org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                 362,
		                                                                                 Short.MAX_VALUE))
		                                                                 .addContainerGap()))));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                              .add(layout.createSequentialGroup().addContainerGap()
		                                         .add(jLabel1)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(jSeparator1,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              10,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(overwriteCheckBox,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              13,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(overwriteComboBox,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              25,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(messageScrollPane,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              103, Short.MAX_VALUE).addContainerGap()));
	} // </editor-fold>

	private void overwriteCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		if (overwriteComboBox.isEnabled() == false) {
			overwriteComboBox.setEnabled(true);
			CytoscapeInit.getProperties()
			             .setProperty("defaultSpeciesName",
			                          (String) overwriteComboBox.getSelectedItem());
		} else {
			overwriteComboBox.setEnabled(false);
			CytoscapeInit.getProperties().setProperty("defaultSpeciesName", currentSpeciesName);
		}
	}

	private void overwriteComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
		String species = (String) overwriteComboBox.getSelectedItem();
		CytoscapeInit.getProperties().setProperty("defaultSpeciesName", species);
	}

	private void setTaxonomyTable() {
		BufferedReader spListReader = null;

		try {
			URL taxURL = getClass().getResource(TAXON_RESOURCE_FILE);
			spListReader = new BufferedReader(new InputStreamReader(taxURL.openStream()));

			System.out.println("Taxonomy table found in jar file...");
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			setSpList(spListReader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setSpList(final BufferedReader rd) throws IOException {
		String curLine = null;
		String name1 = null;

		// remove the first line, which is a title
		curLine = rd.readLine();

		while ((curLine = rd.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(curLine, "|");
			st.nextToken();
			name1 = st.nextToken().trim();

			overwriteComboBox.addItem(name1);
		}
	}

	// Variables declaration - do not modify
	private javax.swing.JComboBox overwriteComboBox;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JCheckBox overwriteCheckBox;
	private javax.swing.JEditorPane messageEditorPane;
	private javax.swing.JScrollPane messageScrollPane;

	// End of variables declaration
}
