package cytoscape.data.servers.ui;

import java.awt.Dimension;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.JDialog;

import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;

/*
 * OntologySourceSelectDialog.java
 *
 * Created on 2006/08/04, 17:32
 */

/**
 * 
 * @author kono
 */
public class OntologySourceSelectDialog extends JDialog {

	private String sourceName;
	private String sourceUrlString;
	
	/** Creates new form OntologySourceSelectDialog */
	public OntologySourceSelectDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		sourceName = null;
		sourceUrlString = null;
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code">
	private void initComponents() {
		titleLabel = new javax.swing.JLabel();
		jSeparator1 = new javax.swing.JSeparator();
		ontologyNameTextField = new javax.swing.JTextField();
		dataSourceTextField = new javax.swing.JTextField();
		cancelButton = new javax.swing.JButton();
		addButton = new javax.swing.JButton();
		browseButton = new javax.swing.JButton();
		nameLabel = new javax.swing.JLabel();
		sourceLabel = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Add new ontology data source");
		titleLabel.setFont(new java.awt.Font("Dialog", 1, 14));
		titleLabel.setText("Add New Ontology Data Source");

		ontologyNameTextField.setText("Enter name for the ontology here.");
		ontologyNameTextField
				.setToolTipText("Add data source from local file system...");
		ontologyNameTextField
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						ontologyNameTextFieldActionPerformed(evt);
					}
				});

		dataSourceTextField.setText("http://");
		dataSourceTextField
				.setToolTipText("http, ftp, and file are supported.");

		cancelButton.setText("Cancel");
		cancelButton.setPreferredSize(new Dimension(75, 25));
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		addButton.setText("Add");
		addButton.setPreferredSize(new java.awt.Dimension(75, 25));
		addButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addButtonActionPerformed(evt);
			}
		});

		browseButton.setText("Browse");
		browseButton.setPreferredSize(new java.awt.Dimension(75, 25));
		browseButton
				.setToolTipText("Add data source from local file system...");
		browseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				browseButtonActionPerformed(evt);
			}
		});

		nameLabel.setText("Ontology Name:");

		sourceLabel.setText("Data Source:");

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																jSeparator1,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																452,
																Short.MAX_VALUE)
														.add(titleLabel)
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																layout
																		.createSequentialGroup()
																		.add(
																				addButton,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				cancelButton))
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				layout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.TRAILING)
																						.add(
																								layout
																										.createSequentialGroup()
																										.add(
																												sourceLabel)
																										.add(
																												35,
																												35,
																												35))
																						.add(
																								layout
																										.createSequentialGroup()
																										.add(
																												nameLabel)
																										.addPreferredGap(
																												org.jdesktop.layout.LayoutStyle.RELATED)))
																		.add(
																				layout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.LEADING)
																						.add(
																								org.jdesktop.layout.GroupLayout.TRAILING,
																								layout
																										.createSequentialGroup()
																										.add(
																												dataSourceTextField,
																												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																												254,
																												Short.MAX_VALUE)
																										.addPreferredGap(
																												org.jdesktop.layout.LayoutStyle.RELATED)
																										.add(
																												browseButton))
																						.add(
																								ontologyNameTextField,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								340,
																								Short.MAX_VALUE))))
										.addContainerGap()));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.add(titleLabel)
										.add(8, 8, 8)
										.add(
												jSeparator1,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																ontologyNameTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(nameLabel))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(browseButton)
														.add(
																dataSourceTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(sourceLabel))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED,
												16, Short.MAX_VALUE)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																cancelButton,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(
																addButton,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addContainerGap()));
		pack();
	}// </editor-fold>

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		dispose();
	}

	private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {
		
		final String name = ontologyNameTextField.getText();
		final String source = this.dataSourceTextField.getText();
		
		
		if (name == null || source == null || name.length() == 0
				|| source.length() == 0) {
			return;
		} else {
			sourceName = name;
			sourceUrlString = source;
		}
		
		setVisible(false);
	}

	private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		CyFileFilter oboFilter = new CyFileFilter();
		oboFilter.addExtension("obo");
		oboFilter.setDescription("Ontology files in OBO format");

		// Get the file name
		File file = FileUtil.getFile("Select local data source file",
				FileUtil.LOAD, new CyFileFilter[] { oboFilter });

		try {
			dataSourceTextField.setText(file.toURL().toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	private void ontologyNameTextFieldActionPerformed(
			java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}
	
	public String getSourceUrlString() {
		return sourceUrlString;
	}
	
	public String getSourceName() {
		return sourceName;
	}

	// Variables declaration - do not modify
	private javax.swing.JButton addButton;
	private javax.swing.JButton browseButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JTextField dataSourceTextField;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JLabel nameLabel;
	private javax.swing.JTextField ontologyNameTextField;
	private javax.swing.JLabel sourceLabel;
	private javax.swing.JLabel titleLabel;
	// End of variables declaration
}