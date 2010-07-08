package cytoscape.visual.customgraphic.ui;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;

import cytoscape.Cytoscape;
import cytoscape.render.stateful.CustomGraphic;
import cytoscape.visual.customgraphic.CustomGraphicsPool;
import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.customgraphic.impl.bitmap.URLImageCustomGraphics;


/**
 * Main UI for managing on-memory library of Custom Graphics
 * 
 * @author kono
 */
public class CustomGraphicsManagerDialog extends javax.swing.JDialog {

	private static final long serialVersionUID = 7681270324415099781L;
	
	// List of graphics available
	private CustomGraphicsBrowser browser;
	
	// Panel for displaying actual size image
	private final CustomGraphicsDetailPanel detail;
	
	// Manager object for on-memory graphics.
	private final CustomGraphicsPool pool;

	/** Creates new form CustomGraphicsManagerDialog */
	public CustomGraphicsManagerDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		pool = Cytoscape.getVisualMappingManager().getCustomGraphicsPool();
		initComponents();
		try {
			browser = new CustomGraphicsBrowser();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		detail = new CustomGraphicsDetailPanel();

		this.leftScrollPane.setViewportView(browser);
		this.rightScrollPane.setViewportView(detail);
		this.setPreferredSize(new Dimension(850, 550));
		this.setTitle("Custom Graphics Manager");

		this.browser.addListSelectionListener(detail);
		pack();

	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		buttonPanel = new javax.swing.JPanel();
		deleteButton = new javax.swing.JButton();
		addButton = new javax.swing.JButton();
		mainSplitPane = new javax.swing.JSplitPane();
		leftScrollPane = new javax.swing.JScrollPane();
		rightScrollPane = new javax.swing.JScrollPane();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		buttonPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		deleteButton.setText("-");
		deleteButton.setToolTipText("Remove selected graphics from library.");
		deleteButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteButtonActionPerformed(evt);
			}
		});

		addButton.setText("+");
		addButton.setToolTipText("Add a folder to Custom Graphics Library");
		addButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addButtonActionPerformed(evt);
			}
		});

		org.jdesktop.layout.GroupLayout buttonPanelLayout = new org.jdesktop.layout.GroupLayout(
				buttonPanel);
		buttonPanel.setLayout(buttonPanelLayout);
		buttonPanelLayout
				.setHorizontalGroup(buttonPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								org.jdesktop.layout.GroupLayout.TRAILING,
								buttonPanelLayout
										.createSequentialGroup()
										.addContainerGap(580, Short.MAX_VALUE)
										.add(
												addButton,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												50,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												deleteButton,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												50,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));
		buttonPanelLayout.setVerticalGroup(buttonPanelLayout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(
						buttonPanelLayout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.BASELINE).add(
								deleteButton).add(addButton)));

		mainSplitPane.setDividerLocation(230);
		mainSplitPane.setLeftComponent(leftScrollPane);
		mainSplitPane.setRightComponent(rightScrollPane);

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				org.jdesktop.layout.GroupLayout.TRAILING, buttonPanel,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.add(mainSplitPane,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 690,
						Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				org.jdesktop.layout.GroupLayout.TRAILING,
				layout.createSequentialGroup().add(mainSplitPane,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 552,
						Short.MAX_VALUE).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						buttonPanel,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));

		pack();
	}// </editor-fold>

	private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// Add a directory
		final JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select Image Directory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setMultiSelectionEnabled(true);

		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
			importFromDirectories(chooser.getSelectedFiles());
	}

	private void importFromDirectories(final File[] directories) {
		for (final File file : directories)
			processFiles(file.listFiles());
	}

	private void processFiles(final File[] files) {
		for (final File file : files) {
			BufferedImage img = null;
			if (file.isFile()) {
				try {
					img = ImageIO.read(file);
				} catch (IOException e) {
					System.err.println("Could not read file: "
							+ file.toString());
					e.printStackTrace();
					continue;
				}
			}

			if (img != null) {
				final CyCustomGraphics cg = new URLImageCustomGraphics(
						file.toString(), img);
				try {
					pool.addGraphics(cg, file.toURI().toURL());
				} catch (MalformedURLException e) {
					e.printStackTrace();
					continue;
				}
				((DefaultListModel) browser.getModel()).addElement(cg);
			}
		}
	}

	private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
		final Object[] toBeRemoved = browser.getSelectedValues();
		for(Object g: toBeRemoved) {
			final CyCustomGraphics cg = (CyCustomGraphics) g;
			browser.removeCustomGraphics(cg);
			pool.removeGraphics(cg.getIdentifier());
		}
	}

	// Variables declaration - do not modify
	private javax.swing.JButton addButton;
	private javax.swing.JPanel buttonPanel;
	private javax.swing.JButton deleteButton;
	private javax.swing.JScrollPane leftScrollPane;
	private javax.swing.JSplitPane mainSplitPane;
	private javax.swing.JScrollPane rightScrollPane;
	// End of variables declaration

}
