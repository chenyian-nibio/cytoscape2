package cytoscape.dialogs.logger;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogHandler;
import cytoscape.logger.LogLevel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.*;

/** Singleton dialog */
public class LoggerDialog extends javax.swing.JDialog implements CyLogHandler,PropertyChangeListener {
    private static LoggerDialog dialog;
    private Map<LogLevel, List<String>> messageMap;
    private Map<LogLevel, JScrollPane> logTabMap;
    private boolean messageAdded = false;

    // Variables declaration - do not modify
    private javax.swing.JButton clearButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JTabbedPane logTabs;

    // aware no one can currently create the console with an owner, gotta see how headless mode works
    protected LoggerDialog(JFrame owner) {
        super(owner, false);
        init();
        
        Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);
    }

    public static LoggerDialog getLoggerDialog() {
        if (dialog == null) {
            dialog = new LoggerDialog(Cytoscape.getDesktop());
            dialog.setTitle("Cytoscape Error Console");
        }

        return dialog;
    }

    private void init() {
        messageMap = new HashMap<LogLevel, List<String>>();
        logTabMap = new HashMap<LogLevel, JScrollPane>();
        initComponents();
        this.setSize(this.getWidth()*2, this.getHeight()*2);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">
    private void initComponents() {
        closeButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        logTabs = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    closeButtonActionPerformed(evt);
                }
            });

        clearButton.setText("Clear Messages");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    clearButtonActionPerformed(evt);
                }
            });

        logTabs.setAutoscrolls(true);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(layout.createSequentialGroup()
                                                   .addContainerGap()
                                                   .add(layout.createParallelGroup(
                        org.jdesktop.layout.GroupLayout.LEADING)
                                                              .add(logTabs,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380,
                        Short.MAX_VALUE)
                                                              .add(org.jdesktop.layout.GroupLayout.TRAILING,
                        layout.createSequentialGroup().add(clearButton)
                              .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                              .add(closeButton))).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING)
                                      .add(org.jdesktop.layout.GroupLayout.TRAILING,
                layout.createSequentialGroup().addContainerGap()
                      .add(logTabs,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263,
                    Short.MAX_VALUE)
                      .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                      .add(layout.createParallelGroup(
                        org.jdesktop.layout.GroupLayout.BASELINE)
                                 .add(closeButton).add(clearButton))
                      .addContainerGap()));
        pack();
    } // </editor-fold>

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {
        for (LogLevel key : this.messageMap.keySet()) {
            this.messageMap.get(key).clear();
            ((JEditorPane) this.logTabMap.get(key).getViewport().getView()).setText(
                "");
        }
    }

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    public void handleLog(LogLevel level, String msg) {
        if (level.getLevel() > 3) {
            return; // not adding messages for "fatal" errors
        }

        if (level.equals(LogLevel.LOG_DEBUG)) {
            // check for a "debugging" mode, if false do not add a tab
        }

        // get or create list of messages
        List<String> Messages = (this.messageMap.get(level) != null)
            ? this.messageMap.get(level) : new ArrayList<String>();

        // Do whatever formatting we need
        msg = msg.replace("\n", "<br>");
        msg = msg.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");

        Messages.add(msg);
        // Make sure it gets added back in
        this.messageMap.put(level, Messages);

        messageAdded = true;

        if (isVisible()) {
            JEditorPane MessagePane = addTab(level);
            StringBuffer sb = createMessages(level);

            MessagePane.setText("");
            MessagePane.setText(sb.toString());
            messageAdded = false;
        } else if (level.equals(LogLevel.LOG_ERROR)) {
            setVisible(true);
        }

        if (((level == LogLevel.LOG_ERROR) || (level == LogLevel.LOG_WARN)) &&
                isVisible()) {
            logTabs.setSelectedComponent(logTabMap.get(level));
        }
    }

    private JEditorPane addTab(LogLevel level) {
        JEditorPane MessagesPane = null;

        if (this.logTabMap.get(level) != null) {
            JScrollPane ScrollPane = this.logTabMap.get(level);
            MessagesPane = (JEditorPane) ScrollPane.getViewport().getView();
        } else {
            JScrollPane ScrollPane = new JScrollPane();
            this.logTabMap.put(level, ScrollPane);
            logTabs.addTab(level.getPrettyName(), ScrollPane);
            MessagesPane = new JEditorPane();
            ScrollPane.setViewportView(MessagesPane);
            MessagesPane.setEditable(false);
            MessagesPane.setContentType("text/html");
        }

        return MessagesPane;
    }

    private void addEmptyMessage() {
        this.addTab(LogLevel.LOG_ERROR);
    }

    private StringBuffer createMessages(LogLevel level) {
        StringBuffer sb = new StringBuffer();
        String color = "#333333";

        if (level.equals(LogLevel.LOG_ERROR)) {
            color = "#ff0000";
        } else if (level.equals(LogLevel.LOG_WARN)) {
            color = "#ff4500";
        }

        sb.append("<html><style type='text/css'>");
        sb.append("body,th,td,div,p,h1,h2,li,dt,dd ");
        sb.append("{ font-family: Tahoma, \"Gill Sans\", Arial, sans-serif; }");
        sb.append("body { margin: 0px; color: " + color +
            "; background-color: #ffffff; }");
        sb.append("#indent { padding-left: 30px; }");
        sb.append("ul {list-style-type: none}");
        sb.append("</style><body>");

        sb.append("<table width='100%' cellspacing='5'>");

        // show the message in reversed order, i.e. most recent message on top
        int line = messageMap.get(level).size();

        for (int i = messageMap.get(level).size()-1; i >= 0; i--) {
        	sb.append("<tr><td width='5%'>" + line + "</td><td width='95%'>");
        	sb.append(messageMap.get(level).get(i));
        	sb.append("</td></tr>");
        	sb.append("<tr><td colspan='2'><hr></td></tr>");
        	line--;
        }        	

        sb.append("</table></body></html>");

        return sb;
    }

    public void setVisible(boolean vis) {
        if (this.messageMap.size() <= 0) {
            this.addEmptyMessage();
        }

        // Have we updated any messages?
        if (messageAdded) {
            Set<LogLevel> levels = this.messageMap.keySet();

            for (LogLevel level : levels) {
                JEditorPane MessagePane = addTab(level);
                StringBuffer sb = createMessages(level);

                MessagePane.setContentType("text/html");
                MessagePane.setText(sb.toString());
            }

            messageAdded = false;
        }

        // Yes, rebuild the list
        super.setVisible(vis);
    }

    
	/**
	 * Property change listener - to clear the console panel after load of new Session.
	 *
	 * @param event PropertyChangeEvent
	 */
	public void propertyChange(PropertyChangeEvent event) {
	
		if (event.getPropertyName().equalsIgnoreCase(Cytoscape.SESSION_LOADED)){
			// perform the same action as clearButton
			try {
				this.clearButtonActionPerformed(null);
			}
			catch (Exception e){
			}
		}
	}
	
    
    /* -------------------------------------- */
    public static void main(String[] args) {
        LoggerDialog dialog = LoggerDialog.getLoggerDialog();
        dialog.setVisible(true);

        dialog.handleLog(LogLevel.LOG_ERROR, "Error, error!");
        dialog.handleLog(LogLevel.LOG_ERROR, "It's gonna blow!!!");

        dialog.handleLog(LogLevel.LOG_WARN, "Canna take much more Cap'n!");
        dialog.handleLog(LogLevel.LOG_WARN, "Foobared");

        dialog.handleLog(LogLevel.LOG_INFO, "Just sayin'...");
    }

    // End of variables declaration
}
