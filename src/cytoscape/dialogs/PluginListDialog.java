/**
 *
 */
package cytoscape.dialogs;

import cytoscape.*;

import cytoscape.plugin.PluginInfo;
import cytoscape.plugin.PluginManager;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;


// TODO Turn the table into a JTree like the install dialog

/**
 * @author skillcoy
 *
 */
public class PluginListDialog extends JDialog {
	private JTable table;
	private JScrollPane tableScroll;
	private JPanel buttonPanel;
	private JButton deleteButton; // TODO button should be inactive when nothing is selected
	private JButton newButton;
	private JButton findUpdateButton;
	private JButton cancelButton;
	private PluginInfo[] tableData;

	/**
	 * Creates a new PluginListDialog object.
	 */
	public PluginListDialog() {
		super(Cytoscape.getDesktop(), "Cytoscape Plugins");
		this.setLocationRelativeTo(Cytoscape.getDesktop());
		this.initDialog();
	}

	private void initDialog() {
		setPreferredSize(new Dimension(500, 400));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new GridBagLayout());

		initTable();
		initButtons();
	}

	private void initTable() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(10, 0, 10, 0);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;

		JPanel TablePanel = new JPanel(new GridBagLayout());

		JLabel Label = new JLabel("Currently Installed Plugins");
		Label.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 24));
		TablePanel.add(Label, gridBagConstraints);

		table = new JTable();
		table.setPreferredScrollableViewportSize(new Dimension(450, 200));
		tableScroll = new JScrollPane(table);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(10, 0, 10, 0);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;

		tableScroll.setViewportView(table);
		tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		TablePanel.add(tableScroll, gridBagConstraints);

		getContentPane().add(TablePanel);
	}

	private void initButtons() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 0, 0, 5);
		gridBagConstraints.gridy = 0;

		// set up button panel
		buttonPanel = new JPanel(new GridBagLayout());

		deleteButton = new JButton("Delete");
		this.deleteAction();
		buttonPanel.add(deleteButton, gridBagConstraints);

		newButton = new JButton("Get New Plugins");
		newButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent Event) {
					//PluginListDialog.this.dispose();
					PluginManager Mgr = PluginManager.getPluginManager();
					PluginInstallDialog Install = new PluginInstallDialog();

					Map<String, List<PluginInfo>> Plugins = Mgr.getPluginsByCategory();
					Iterator<String> pI = Plugins.keySet().iterator();
					int index = 0;

					while (pI.hasNext()) {
						String Category = pI.next();
						Install.addCategory(Category, Plugins.get(Category), index);

						if (index <= 0)
							index++; // apparenlty just need 0/1
					}

					Install.pack();
					Install.setVisible(true);
				}
			});
		buttonPanel.add(newButton, gridBagConstraints);

		findUpdateButton = new JButton("Find Updates");
		findUpdateButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent Event) {
					// TODO search each plugin for updates
					System.out.println("will search for updates");
				}
			});
		buttonPanel.add(findUpdateButton, gridBagConstraints);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent Event) {
					PluginListDialog.this.dispose();
				}
			});
		buttonPanel.add(cancelButton, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(10, 0, 10, 0);
		getContentPane().add(buttonPanel, gridBagConstraints);
	}

	/**
	 *
	 * @param ColumnNames
	 *         See JTable
	 * @param Data
	 *         See JTable
	 * @param CenterCols
	 *         int array of indecies to the columns that should be centered
	 */
	public void createTable(PluginInfo[] InfoObjs) {
		tableData = InfoObjs;

		String[] ColNames = new String[] { "Plugin Name", "Version", "Category" };
		String[][] Data = new String[tableData.length][ColNames.length];

		for (int i = 0; i < tableData.length; i++) {
			Data[i][0] = tableData[i].getName();
			Data[i][1] = tableData[i].getPluginVersion();
			Data[i][2] = tableData[i].getCategory();
		}

		createTable(ColNames, Data, new int[] { 1 });
	}

	private void createTable(String[] ColumnNames, Object[][] Data, int[] CenterCols) {
		TableModel Model = new TableModel(ColumnNames, Data);
		table.setModel(Model);

		//  Create cell renderer
		TableCellRenderer centerRenderer = new CenterRenderer();

		for (int i = 0; i < CenterCols.length; i++) {
			TableColumn column = table.getColumnModel().getColumn(CenterCols[i]);
			column.setCellRenderer(centerRenderer);
		}

		initColumnSizes();
	}

	private void initColumnSizes() {
		TableModel model = (TableModel) table.getModel();
		TableColumn column = null;
		Component comp = null;
		int headerWidth = 0;
		int cellWidth = 0;
		Object[] longValues = model.getLongestValues();
		TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

		for (int i = 0; i < longValues.length; i++) {
			column = table.getColumnModel().getColumn(i);

			comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(),
			                                                    false, false, 0, 0);
			headerWidth = comp.getPreferredSize().width;

			comp = table.getDefaultRenderer(model.getColumnClass(i))
			            .getTableCellRendererComponent(table, longValues[i], false, false, 0, i);
			cellWidth = comp.getPreferredSize().width;

			column.setPreferredWidth(Math.max(headerWidth, cellWidth));
		}
	}

	private void deleteAction() {
		deleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent Event) {
					boolean delete = false;
					int Row = table.getSelectedRow();
					PluginInfo Obj = PluginListDialog.this.tableData[Row];
					System.out.println("Deleting " + Obj.getName());

					String Msg = "This is a 'core' plugin and other plugins may depend on it, are you sure you want to delete it?";

					if (Obj.getCategory().equalsIgnoreCase("core")) {
						if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Cytoscape
						                                                                                                                                                                                                                   .getDesktop(),
						                                                            Msg,
						                                                            "Core Plugin Removal",
						                                                            JOptionPane.YES_NO_OPTION,
						                                                            JOptionPane.QUESTION_MESSAGE))
							delete = true;
					} else
						delete = true;

					if (delete) {
						if (PluginManager.getPluginManager().delete(Obj))
							JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
							                              "Plugin " + Obj.getName()
							                              + " successfully deleted.",
							                              "Plugin Deletion",
							                              JOptionPane.PLAIN_MESSAGE);
						else
							JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
							                              "Failed to delete plugin "
							                              + Obj.getName()
							                              + ".  You may need to manually delete it.");

						PluginListDialog.this.dispose();
					}

					// TODO how do I remove a row from the table?
					((PluginListDialog.TableModel) table.getModel()).removeRow(Row);
				}
			});
	}

	private class TableModel extends AbstractTableModel {
		private String[] ColNames;
		private Object[][] RowData;

		public TableModel(String[] ColumnNames, Object[][] Data) {
			this.ColNames = ColumnNames;
			this.RowData = Data;
		}

		public int getColumnCount() {
			return ColNames.length;
		}

		public String getColumnName(int index) {
			return ColNames[index];
		}

		public int getRowCount() {
			return RowData.length;
		}

		public Object getValueAt(int row, int col) {
			return RowData[row][col];
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

		public void setValueAt(Object Value, int row, int col) { // TODO fix this, it's not setting correctly
			                                                     //RowData.add(row, Value);
			RowData[row][col] = Value;
			fireTableCellUpdated(row, col);
		}

		public void removeRow(int row) { // this doesn't actually work....causes a nasty exception (is there another kind??)
			removeRow(row);
			fireTableRowsDeleted(row, row);
		}

		public Object[] getLongestValues() {
			int[] Longest = new int[RowData[0].length];
			Object[] LongestValues = new Object[RowData[0].length];

			for (int i = 0; i < RowData.length; i++) {
				for (int n = 0; n < RowData[i].length; n++) {
					if (((String) RowData[i][n]).length() > Longest[n]) {
						Longest[n] = ((String) RowData[i][n]).length();
						LongestValues[n] = (String) RowData[i][n];
					}
				}
			}

			return LongestValues;
		}
	}

	// this is really only used to center the version numbers 
	class CenterRenderer extends DefaultTableCellRenderer {
		public CenterRenderer() {
			setHorizontalAlignment(CENTER);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus,
		                                               int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			return this;
		}
	}
}