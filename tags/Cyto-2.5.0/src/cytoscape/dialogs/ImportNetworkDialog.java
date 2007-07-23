/*
 * NetworkImportDialog.java
 *
 * Created on 2006/05/08, 11:33
 */
package cytoscape.dialogs;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ToolTipManager;
import javax.xml.bind.JAXBException;

import cytoscape.Cytoscape;
import cytoscape.bookmarks.Bookmarks;
import cytoscape.bookmarks.Category;
import cytoscape.bookmarks.DataSource;
import cytoscape.data.ImportHandler;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.BookmarksUtil;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;


/**
 *
 * @author kono
 */
public class ImportNetworkDialog extends JDialog implements java.awt.event.ActionListener {
	private boolean status;
	private File[] networkFiles;
	private Bookmarks theBookmarks = null; // get it from session
	private String bookmarkCategory = "network";
	private String URLstr;
	private BookmarkComboBoxEditor bookmarkEditor = new BookmarkComboBoxEditor();
	private String pleaseMessage = "Please provide URL or select from list";
	
	private static final String URL_TOOLTIP = "<html>Enter URL or <strong><font color=\"red\">Drag and Drop local/remote files.</font></strong></html>";
	private static final String LOCAL_TOOLTIP = "<html>Specify path to local files.</html>";
	

	/** Creates new form NetworkImportDialog
	 * @throws IOException
	 * @throws JAXBException */
	public ImportNetworkDialog(java.awt.Frame parent, boolean modal)
	    throws JAXBException, IOException {
		super(parent, modal);

		setTitle("Import Network");
		initComponents();
		addListeners();

		// By default, import from local
		switchImportView("Local");

		status = false;
		networkFiles = null;

		theBookmarks = Cytoscape.getBookmarks();

		// if theBookmarks doesnot exist, create an empty one
		if (theBookmarks == null) {
			theBookmarks = new Bookmarks();
			Cytoscape.setBookmarks(theBookmarks);
		}

		// if bookmarkCategory "network" does not exist, create a "network" with
		// empty DataSource
		Category theCategory = BookmarksUtil.getCategory(bookmarkCategory,
		                                                 theBookmarks.getCategory());

		if (theCategory == null) {
			theCategory = new Category();
			theCategory.setName(bookmarkCategory);

			List<Category> theCategoryList = theBookmarks.getCategory();
			theCategoryList.add(theCategory);
		}
		
		
	}

	/**
	 * Get first file only.
	 *
	 * @return
	 */
	public File getFile() {
		if ((networkFiles != null) && (networkFiles.length > 0)) {
			return networkFiles[0];
		} else {
			return null;
		}
	}

	/**
	 * Get all files selected.
	 *
	 * @return
	 */
	public File[] getFiles() {
		return networkFiles;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean getStatus() {
		return status;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isRemote() {
		return remoteRadioButton.isSelected();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getURLStr() {
		return URLstr;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */

	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		

		buttonGroup1 = new javax.swing.ButtonGroup();
		titleLabel = new javax.swing.JLabel();
		localRadioButton = new javax.swing.JRadioButton();
		remoteRadioButton = new javax.swing.JRadioButton();
		networkFileTextField = new javax.swing.JTextField();
		networkFileComboBox = new javax.swing.JComboBox();
		selectButton = new javax.swing.JButton();
		importButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		titleSeparator = new javax.swing.JSeparator();
		radioButtonPanel = new javax.swing.JPanel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		titleLabel.setFont(new java.awt.Font("Sans-Serif", Font.BOLD, 14));
		titleLabel.setText("Import Network File");

		radioButtonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Source Type"));
		buttonGroup1.add(localRadioButton);
		localRadioButton.setSelected(true);
		localRadioButton.setText("Local");
		localRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		localRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		localRadioButton.setToolTipText(LOCAL_TOOLTIP);

		buttonGroup1.add(remoteRadioButton);
		remoteRadioButton.setText("Remote/URL");
		remoteRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		remoteRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		remoteRadioButton.setToolTipText(URL_TOOLTIP);
		
		networkFileTextField.setText("Please select a network file...");
		networkFileTextField.setName("networkFileTextField");

		selectButton.setText("Select");

		importButton.setText("Import");
		importButton.setName("btnImport");

		cancelButton.setText("Cancel");
		cancelButton.setName("btnCancel");

		networkFileComboBox.setRenderer(new MyCellRenderer());
		networkFileComboBox.setEditor(bookmarkEditor);
		networkFileComboBox.setEditable(true);
		networkFileComboBox.setName("networkFileComboBox");
		networkFileComboBox.setToolTipText("<html><body>You can specify URL by the following:<ul><li>Type URL</li><li>Select from pull down menu</li><li>Drag & Drop URL from Web Browser</li></ul></body><html>");
		final ToolTipManager tp = ToolTipManager.sharedInstance();
		tp.setInitialDelay(1);
		tp.setDismissDelay(7500);
		
		
		org.jdesktop.layout.GroupLayout radioButtonPanelLayout = new org.jdesktop.layout.GroupLayout(radioButtonPanel);
		radioButtonPanel.setLayout(radioButtonPanelLayout);
		radioButtonPanelLayout.setHorizontalGroup(radioButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                                .add(radioButtonPanelLayout.createSequentialGroup()
		                                                                                           .addContainerGap()
		                                                                                           .add(localRadioButton)
		                                                                                           .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                                                                           .add(remoteRadioButton)
		                                                                                           .addContainerGap(250,
		                                                                                                            Short.MAX_VALUE)));
		radioButtonPanelLayout.setVerticalGroup(radioButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                              .add(radioButtonPanelLayout.createSequentialGroup()
		                                                                                         .add(radioButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                                                                                    .add(localRadioButton)
		                                                                                                                    .add(remoteRadioButton))));

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                .add(layout.createSequentialGroup().addContainerGap()
		                                           .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                      .add(networkFileComboBox, 0, 350,
		                                                           Short.MAX_VALUE)
		                                                      .add(titleLabel,
		                                                           org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                           350,
		                                                           org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                                      .add(titleSeparator,
		                                                           org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                           350, Short.MAX_VALUE)
		                                                      .add(radioButtonPanel,
		                                                           org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                           org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                           Short.MAX_VALUE)
		                                                      .add(layout.createSequentialGroup()
		                                                                 .add(networkFileTextField,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      350, Short.MAX_VALUE)
		                                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                                                 .add(selectButton))
		                                                      .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                                           layout.createSequentialGroup()
		                                                                 .add(importButton)
		                                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                                                 .add(cancelButton)))
		                                           .addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                              .add(layout.createSequentialGroup().addContainerGap()
		                                         .add(titleLabel).add(8, 8, 8)
		                                         .add(titleSeparator,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .add(7, 7, 7)
		                                         .add(radioButtonPanel,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                    .add(selectButton)
		                                                    .add(networkFileTextField))
		                                         .add(networkFileComboBox,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
		                                                          3, Short.MAX_VALUE)
		                                         .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                    .add(cancelButton).add(importButton))
		                                         .addContainerGap()));
		pack();
	} // </editor-fold>

	private void addListeners() {
		LocalRemoteListener l = new LocalRemoteListener();
		localRadioButton.addActionListener(l);
		remoteRadioButton.addActionListener(l);

		// ButtonActionListener btnActionListener = new ButtonActionListener();
		selectButton.addActionListener(this);
		cancelButton.addActionListener(this);
		importButton.addActionListener(this);

		bookmarkEditor.addActionListener(this);
	}

	private void switchImportView(String pLocation) {
		if (pLocation.equalsIgnoreCase("Local")) {
			// for the case of local import
			networkFileComboBox.setVisible(false);

			networkFileTextField.setVisible(true);
			selectButton.setVisible(true);
		} else { // Remote
			networkFileComboBox.setVisible(true);

			networkFileTextField.setVisible(false);
			selectButton.setVisible(false);

			loadBookmarkCMBox();
		}
	}

	private void loadBookmarkCMBox() {
		networkFileComboBox.removeAllItems();

		DefaultComboBoxModel theModel = new DefaultComboBoxModel();

		DataSource firstDataSource = new DataSource();
		firstDataSource.setName("");
		firstDataSource.setHref(null);
		
		theModel.addElement(firstDataSource);

		// Extract the URL entries
		List<DataSource> theDataSourceList = BookmarksUtil.getDataSourceList(bookmarkCategory,
		                                                                     theBookmarks
		                                                                                                                                                                                                                                                                                                                                                                            .getCategory());

		if (theDataSourceList != null) {
			for (int i = 0; i < theDataSourceList.size(); i++) {
				theModel.addElement(theDataSourceList.get(i));
			}
		}

		networkFileComboBox.setModel(theModel);
	}

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		status = false;
		this.dispose();
	}

	private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {
		status = true;
		this.dispose();
	}

	private void selectNetworkFileButtonActionPerformed(java.awt.event.ActionEvent evt) {
		CyFileFilter[] tempCFF = (CyFileFilter[]) Cytoscape.getImportHandler()
		                                                   .getAllFilters(ImportHandler.GRAPH_NATURE)
		                                                   .toArray(new CyFileFilter[0]);

		networkFiles = FileUtil.getFiles("Import Network Files", FileUtil.LOAD, tempCFF);

		if (networkFiles != null) {
			/*
			 * Accept multiple files
			 */
			StringBuffer fileNameSB = new StringBuffer();
			StringBuffer tooltip = new StringBuffer();
			tooltip.append("<html><body><strong><font color=RED>The following files will be loaded:</font></strong><br>");

			for (int i = 0; i < networkFiles.length; i++) {
				fileNameSB.append(networkFiles[i].getAbsolutePath() + ", ");
				tooltip.append("<p>" + networkFiles[i].getAbsolutePath() + "</p>");
			}

			tooltip.append("</body></html>");
			networkFileTextField.setText(fileNameSB.toString());
			networkFileTextField.setToolTipText(tooltip.toString());

			importButton.setEnabled(true);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) {
		Object _actionObject = e.getSource();

		// handle Button events
		if (_actionObject instanceof JButton) {
			JButton _btn = (JButton) _actionObject;

			// process radio button events
			if (_btn == selectButton) {
				selectNetworkFileButtonActionPerformed(e);
			} else if (_btn == importButton) {
				if (localRadioButton.isSelected()) // local import
				 {
					importButtonActionPerformed(e);
				} else // case for remote import
				 {
					//doURLimport(e);
					createURLimportTask(e);
				}
			} else if (_btn == cancelButton) {
				cancelButtonActionPerformed(e);
			}
		}

		if (_actionObject instanceof JTextField) {
			createURLimportTask(e);
		}
	}

	private void createURLimportTask(java.awt.event.ActionEvent e) {
		String theURLstr = bookmarkEditor.getURLstr().trim();
		URL theURL = null;

		try {
			theURL = new URL(theURLstr);
		} catch (MalformedURLException e1) {
			JOptionPane.showMessageDialog(this, "URL error!", "Warning",
			                              JOptionPane.INFORMATION_MESSAGE);

			return;
		}

		// Create Task
		Task task = new URLdownloadTask(theURL, e);

		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(false);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(true);
		jTaskConfig.displayCancelButton(true);

		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}

	private class URLdownloadTask implements Task {
		private TaskMonitor taskMonitor;
		URL url;
		java.awt.event.ActionEvent e;

		public URLdownloadTask(URL url, java.awt.event.ActionEvent e) {
			this.url = url;
			this.e = e;
		}

		public void run() {
			taskMonitor.setStatus("Downloading ...");
			taskMonitor.setPercentCompleted(-1);

			doURLimport(url, e, taskMonitor);

			taskMonitor.setPercentCompleted(100);
			taskMonitor.setStatus("Downloading successfully from " + url.toString());
		}

		public void halt() {
		}

		public void setTaskMonitor(TaskMonitor monitor) throws IllegalThreadStateException {
			this.taskMonitor = monitor;
		}

		public String getTitle() {
			return "URL downloading";
		}
	}

	private void doURLimport(URL url, java.awt.event.ActionEvent e, TaskMonitor taskMonitor) {
		cytoscape.data.ImportHandler theHandler = Cytoscape.getImportHandler();

		File tmpFile = null;

		try {
			tmpFile = theHandler.downloadFromURL(url, taskMonitor);
		} catch (MalformedURLException e1) {
			JOptionPane.showMessageDialog(this, "URL error!", "Warning",
			                              JOptionPane.INFORMATION_MESSAGE);
		} catch (FileNotFoundException e2) {
			JOptionPane.showMessageDialog(this,
			                              "File was not found! Please make sure the URL is correct!",
			                              "Warning", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e3) {
			JOptionPane.showMessageDialog(this,
			                              "IO error! May caused by server-name, proxy, write-permission.",
			                              "Warning", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e4) {
			JOptionPane.showMessageDialog(this, "Failed to download from URL!", "Warning",
			                              JOptionPane.INFORMATION_MESSAGE);
		}

		if (tmpFile == null) {
			return;
		}

		networkFiles = new File[1];
		networkFiles[0] = tmpFile;

		importButtonActionPerformed(e);
	}

	class LocalRemoteListener implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			Object _actionObject = e.getSource();

			// handle radioButton events
			if (_actionObject instanceof JRadioButton) {
				JRadioButton _rbt = (JRadioButton) _actionObject;

				// process radio button events
				if (_rbt == localRadioButton) {
					switchImportView("Local");
				} else { // from rbtRemote
					switchImportView("Remote");
				}

				pack();
			}
		} // actionPerformed()
	}

	class MyCellRenderer extends JLabel implements ListCellRenderer {
		public MyCellRenderer() {
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index,
		                                              boolean isSelected, boolean cellHasFocus) {
			DataSource dataSource = (DataSource) value;
			setText(dataSource.getName());

			if (isSelected) {
				if (0 < index) {
					list.setToolTipText(dataSource.getHref());
				}
			}

			return this;
		}
	} // MyCellRenderer

	class BookmarkComboBoxEditor implements javax.swing.ComboBoxEditor {
		DataSource theDataSource = new DataSource();
		JTextField tfInput = new JTextField(pleaseMessage);

		public String getURLstr() {
			return tfInput.getText();
		}

		public void addActionListener(ActionListener l) {
			tfInput.addActionListener(l);
		}

		public void addKeyListener(KeyListener l) {
			tfInput.addKeyListener(l);
		}

		public Component getEditorComponent() {
			return tfInput;
		}

		public Object getItem() {
			return theDataSource;
		}

		public void removeActionListener(ActionListener l) {
		}

		public void selectAll() {
		}

		public void setItem(Object anObject) {
			if (anObject == null) {
				return;
			}

			if (anObject instanceof DataSource) {
				theDataSource = (DataSource) anObject;
				tfInput.setText(theDataSource.getHref());
			}
		}
	} // BookmarkComboBoxEditor

	// Variables declaration - do not modify
	private javax.swing.JButton cancelButton;
	private javax.swing.JButton importButton;
	private JPanel radioButtonPanel;
	private javax.swing.JButton selectButton;
	private javax.swing.ButtonGroup buttonGroup1;
	private javax.swing.JComboBox networkFileComboBox;
	private javax.swing.JLabel titleLabel;
	private javax.swing.JRadioButton remoteRadioButton;
	private javax.swing.JRadioButton localRadioButton;
	private javax.swing.JTextField networkFileTextField;
	private javax.swing.JSeparator titleSeparator;

	// End of variables declaration
}
