package cytoscape.dialogs.plugins;

import javax.swing.JFrame;
import javax.swing.JDialog;

public class LicenseDialog extends javax.swing.JDialog {
	private static String title = "Plugin License Agreement";
	
	
	/** Creates new form LicenseDialog */
	public LicenseDialog() {
		setModal(true);
		setTitle(title);
		setLocationRelativeTo(this);
		initComponents();
	}

	public LicenseDialog(JDialog owner) {
		super(owner, title, true);
		setLocationRelativeTo(this);
		initComponents();
	}

	public LicenseDialog(JFrame owner) {
		super(owner, title, true);
		setLocationRelativeTo(this);
		initComponents();
	}

	public void addLicenseText(String Text) {
		licenseEditorPane.setContentType("text/html");
		String Html = "<html><style type='text/css'>";
		Html += "body,th,td,div,p,h1,h2,li,dt,dd ";
		Html += "{ font-family: Tahoma, \"Gill Sans\", Arial, sans-serif; }";
		Html += "body { margin: 0px; color: #333333; background-color: #ffffff; }";
		Html += "#indent { padding-left: 30px; }";
		Html += "ul {list-style-type: none}";
		Html += "</style><body>";
		Html += Text + "</body></html>";
		licenseEditorPane.setText(Html);
		licenseEditorPane.setEditable(false);
	}

	public void setPluginName(String Name) {
		pluginLicenseLabel.setText(Name + " " + title);
	}
	
	public void addListenerToFinish(java.awt.event.ActionListener listener) {
		finishButton.addActionListener(listener);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		jScrollPane1 = new javax.swing.JScrollPane();
		licenseEditorPane = new javax.swing.JEditorPane();
		agreeRadio = new javax.swing.JRadioButton();
		declineRadio = new javax.swing.JRadioButton();
		finishButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		pluginLicenseLabel = new javax.swing.JLabel();
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		jScrollPane1.setViewportView(licenseEditorPane);
		agreeRadio.setText("Accept");
		agreeRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0,
				0, 0));
		agreeRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));
		agreeRadio.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				agreeRadioStateChanged(evt);
			}
		});
		declineRadio.setText("Decline");
		declineRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
				0, 0, 0));
		declineRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));
		declineRadio.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				declineRadioStateChanged(evt);
			}
		});
		finishButton.setText("Finish");
		finishButton.setEnabled(false);

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});
		pluginLicenseLabel.setText("Plugin License Agreement");
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
																layout
																		.createParallelGroup(
																				org.jdesktop.layout.GroupLayout.LEADING)
																		.add(
																				jScrollPane1,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				363,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																		.add(
																				org.jdesktop.layout.GroupLayout.TRAILING,
																				layout
																						.createSequentialGroup()
																						.add(
																								agreeRadio)
																						.add(
																								30,
																								30,
																								30)
																						.add(
																								declineRadio)
																						.add(
																								42,
																								42,
																								42)
																						.add(
																								finishButton)
																						.add(
																								30,
																								30,
																								30)
																						.add(
																								cancelButton)))
														.add(pluginLicenseLabel))
										.addContainerGap(24, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().add(12, 12, 12).add(
						pluginLicenseLabel).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						jScrollPane1,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 225,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(15,
						15, 15).add(
						layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.BASELINE).add(
								cancelButton).add(finishButton).add(
								declineRadio).add(agreeRadio)).addContainerGap(
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE)));
		pack();
	}// </editor-fold>

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		dispose();
	}

	private void finishButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("FINISHED");
		dispose();
	}

	private void declineRadioStateChanged(javax.swing.event.ChangeEvent evt) {
		if (declineRadio.isSelected()) {
			agreeRadio.setSelected(false);
			finishButton.setEnabled(false);
		}
	}

	private void agreeRadioStateChanged(javax.swing.event.ChangeEvent evt) {
		if (agreeRadio.isSelected()) {
			declineRadio.setSelected(false);
			finishButton.setEnabled(true);
		}
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new LicenseDialog().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify
	private javax.swing.JRadioButton agreeRadio;

	private javax.swing.JButton cancelButton;

	private javax.swing.JRadioButton declineRadio;

	private javax.swing.JButton finishButton;

	private javax.swing.JScrollPane jScrollPane1;

	private javax.swing.JEditorPane licenseEditorPane;

	private javax.swing.JLabel pluginLicenseLabel;
	// End of variables declaration
}
