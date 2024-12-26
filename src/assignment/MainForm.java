package assignment;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 *
 * @author SOF/20/B1/03 - Sandun
 */
public class MainForm {

    final JFrame mainFrame;
    private JMenuBar menuBar;
    private JMenu menuFile, menuView, menuQuery, menuHelp;
    private JMenuItem miConnect, miDisconnect, miSave, miExit, miExecute, miAbout, miMaximize;
    private JPanel pnlMenuBar, pnlToolBar, pnlExploreArea1, pnlExploreArea2, pnlViewer, pnlCommandArea, pnlFooter;
    private JLabel lblMenuBar, lblToolBar, lblExploreArea1, lblExploreArea2, lblViewer, lblCommandArea, lblFooter;
    private JSplitPane spExploreArea, spMainArea, spCommandArea;
    private ImageIcon icoConnect, icoDisconnect, icoExit, icoExecute, icoAbout, icoMaximize;
    private DefaultListModel<String> dbServerListModel;
    private JList<String> dbServerList;
    private List<DatabaseConnector> connectedDatabases = new ArrayList<>();
    private DatabaseTreeManager databaseTreeManager;
    public JTextArea txtCommandArea;
    private JButton btnExecuteQuery, btnDisconnectServer, btnRefresh;

    public MainForm() {

        if (this.connectedDatabases == null) {
            this.connectedDatabases = new ArrayList<>();
        }

        this.mainFrame = new JFrame();
        this.mainFrame.setTitle("Database Management Tool | Assignment 04 - SOF/20/B1/03");
        this.mainFrame.setSize(1000, 700);
        this.mainFrame.setLocationRelativeTo(null);
//        this.mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.mainFrame.setLayout(new BorderLayout());

        // Menu Bar
        menuBar = new JMenuBar();
        pnlMenuBar = new JPanel();

        menuFile = new JMenu("File");
        menuView = new JMenu("View");
        menuQuery = new JMenu("Query");
        menuHelp = new JMenu("Help");

        menuBar.add(menuFile);
        menuBar.add(menuView);
        menuBar.add(menuQuery);
        menuBar.add(menuHelp);

        icoConnect = CustomComponents.scaleIcon(new ImageIcon(getClass().getResource("/icons/connect.png")), 20, 20);
        icoDisconnect = CustomComponents.scaleIcon(new ImageIcon(getClass().getResource("/icons/disconnect.png")), 20, 20);
        icoExit = CustomComponents.scaleIcon(new ImageIcon(getClass().getResource("/icons/exit.png")), 20, 20);
        icoExecute = CustomComponents.scaleIcon(new ImageIcon(getClass().getResource("/icons/execute.png")), 20, 20);
        icoAbout = CustomComponents.scaleIcon(new ImageIcon(getClass().getResource("/icons/about.png")), 20, 20);
        icoMaximize = CustomComponents.scaleIcon(new ImageIcon(getClass().getResource("/icons/maximize.png")), 20, 20);

        // File Menu
        miConnect = new JMenuItem("Connect", icoConnect);
        miDisconnect = new JMenuItem("Disconnect", icoDisconnect);
        miDisconnect.setEnabled(false);
        miExit = new JMenuItem("Exit", icoExit);

        menuFile.add(miConnect);
        menuFile.add(miDisconnect);
        menuFile.addSeparator();
        menuFile.add(miExit);

        // View Menu
        miMaximize = new JMenuItem("Maximize", icoMaximize);
        menuView.add(miMaximize);

        // Query Menu
        miExecute = new JMenuItem("Execute", icoExecute);
        miExecute.setEnabled(false);

        menuQuery.add(miExecute);

        // Help Menu
        miAbout = new JMenuItem("About", icoAbout);
        menuHelp.add(miAbout);

        // Menu Bar Buttons Actions
        miExecute.addActionListener(e -> executeSQL());
        miConnect.addActionListener(e -> openDatabaseConnectionDialog());
        miDisconnect.addActionListener(e -> disconnectDatabaseServer());
        miExit.addActionListener(e -> endApplication());
        miMaximize.addActionListener(e -> setMaximize());
        miAbout.addActionListener(e -> showAbout());

        this.mainFrame.setJMenuBar(menuBar);

        // Standard Tool Bar
        pnlToolBar = new JPanel();
        pnlToolBar.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton btnNew = CustomComponents.customToolbarButton("/icons/connect.png", "Connect", 25, 25);
        JButton btnOpen = CustomComponents.customToolbarButton("/icons/database.png", "Export Database", 24, 25);
        JSeparator verticalSeparator = CustomComponents.customSeparator(SwingConstants.VERTICAL, 2, 30);
        btnExecuteQuery = CustomComponents.customToolbarButton("/icons/execute.png", "Execute", 25, 25);
        btnExecuteQuery.setEnabled(false);
        btnRefresh = CustomComponents.customToolbarButton("/icons/refresh.png", "Refresh", 25, 25);
        btnRefresh.setEnabled(false);
        btnDisconnectServer = CustomComponents.customToolbarButton("/icons/disconnect.png", "Disconnect Server", 25, 25);
        btnDisconnectServer.setEnabled(false);
        JSeparator verticalSeparator2 = CustomComponents.customSeparator(SwingConstants.VERTICAL, 2, 30);

        pnlToolBar.add(btnNew);
        pnlToolBar.add(verticalSeparator);
        pnlToolBar.add(btnOpen);
        pnlToolBar.add(btnExecuteQuery);
        pnlToolBar.add(btnRefresh);
        pnlToolBar.add(verticalSeparator2);
        pnlToolBar.add(btnDisconnectServer);

        btnNew.addActionListener(e -> openDatabaseConnectionDialog());
        btnDisconnectServer.addActionListener(e -> disconnectDatabaseServer());
        btnRefresh.addActionListener(e -> refreshDatabasesView());

        this.mainFrame.add(pnlToolBar, BorderLayout.NORTH);

        // Explore Area 1
        /*
        pnlExploreArea1 = new JPanel(new BorderLayout());
        lblExploreArea1 = new JLabel("Explore Area 1", JLabel.CENTER);
        lblExploreArea1.setFont(lblExploreArea1.getFont().deriveFont(15f));
        pnlExploreArea1.add(lblExploreArea1, BorderLayout.CENTER);
         */
        pnlExploreArea1 = new JPanel(new BorderLayout());
        pnlExploreArea1.setBorder(new TitledBorder("Connected Database Servers"));

        // Create list model and JList
        dbServerListModel = new DefaultListModel<>();
        dbServerList = new JList<>(dbServerListModel);
        dbServerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dbServerList.setCellRenderer(new CustomComponents.DatabaseListCellRenderer());

        // Add scroll pane to explore area
        JScrollPane scrollPane = new JScrollPane(dbServerList);
        pnlExploreArea1.add(scrollPane, BorderLayout.CENTER);

        // Explore Area 2
        pnlExploreArea2 = new JPanel(new BorderLayout());
        pnlExploreArea2.setBorder(new TitledBorder("Databases"));
        lblExploreArea2 = new JLabel("Explore Area 2", JLabel.CENTER);
        lblExploreArea2.setFont(lblExploreArea2.getFont().deriveFont(15f));
        pnlExploreArea2.add(lblExploreArea2, BorderLayout.CENTER);

        // SplitPanel for in between Explore 1 | Explore 2
        spExploreArea = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnlExploreArea1, pnlExploreArea2);
        spExploreArea.setResizeWeight(0.5);
        spExploreArea.setContinuousLayout(true);
        spExploreArea.setDividerSize(4);

        // Viewer or Editing Area
        pnlViewer = new JPanel(new BorderLayout());
        pnlViewer.setBorder(new TitledBorder("Viewer Area"));
        lblViewer = new JLabel("Nothing to view here", JLabel.CENTER);
        lblViewer.setFont(lblViewer.getFont().deriveFont(15f));
        pnlViewer.add(lblViewer, BorderLayout.CENTER);
        this.mainFrame.add(pnlViewer);

        // SplitPanel for in between Explore Area | Viewer Area
        spMainArea = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spExploreArea, pnlViewer);
        spMainArea.setResizeWeight(0.07);
        spMainArea.setContinuousLayout(true);
        spMainArea.setDividerSize(4);

        // Command Area
        pnlCommandArea = new JPanel(new BorderLayout());
        pnlCommandArea.setBorder(new TitledBorder("Command Area"));
        lblCommandArea = new JLabel("Command Area", JLabel.CENTER);
//        lblCommandArea.setFont(lblCommandArea.getFont().deriveFont(15f));

        txtCommandArea = new JTextArea();
        txtCommandArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtCommandArea.setMargin(new Insets(5, 5, 5, 5));
        JScrollPane scrollCommandArea = new JScrollPane(txtCommandArea);
        pnlCommandArea.setPreferredSize(new Dimension(pnlCommandArea.getWidth(), 360));

        pnlCommandArea.add(scrollCommandArea, BorderLayout.CENTER);

        btnExecuteQuery.addActionListener(e -> executeSQL());

        // SplitPanel for in between Viewer Area | Command Area
        spCommandArea = new JSplitPane(JSplitPane.VERTICAL_SPLIT, spMainArea, pnlCommandArea);
        spCommandArea.setResizeWeight(0.8);
        spCommandArea.setContinuousLayout(true);
        spCommandArea.setDividerSize(4);

        // Footer Area
        pnlFooter = new JPanel();
        pnlFooter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        lblFooter = new JLabel("No database selected");
        lblFooter.setFont(new Font("SansSerif", Font.PLAIN, 12));
        pnlFooter.add(lblFooter);
        mainFrame.add(pnlFooter, BorderLayout.SOUTH);

        // Adding SplitPane
        mainFrame.add(spCommandArea, BorderLayout.CENTER);
        this.databaseTreeManager = new DatabaseTreeManager(pnlExploreArea2, this);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mainFrame.setVisible(true);

    }

    private void openDatabaseConnectionDialog() {
        JDialog dialog = new JDialog(mainFrame, "Connect Database Server", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setLayout(new BorderLayout(10, 10));

        // Input Fields Panel
        JPanel pnlInputFields = new JPanel(new GridLayout(6, 2, 10, 10));
        pnlInputFields.setBorder(BorderFactory.createTitledBorder("Connection Details"));
        pnlInputFields.setOpaque(true);

        JLabel lblHost = new JLabel("Host:");
        JTextField txtHost = new JTextField();
        JLabel lblDBName = new JLabel("Database Name (Optional):");
        JTextField txtDBName = new JTextField();
        JLabel lblUsername = new JLabel("Username:");
        JTextField txtUsername = new JTextField();
        JLabel lblPassword = new JLabel("Password:");
        JPasswordField txtPassword = new JPasswordField();
        JLabel lblPort = new JLabel("Port:");
        JTextField txtPort = new JTextField();

        JLabel lblDBType = new JLabel("Select Database Type:");
        String[] dbTypes = {"MySQL", "MSSQL", "OracleDB"};
        JComboBox<String> cbDatabaseType = new JComboBox<>(dbTypes);

        // Adding Components to Input Panel
        pnlInputFields.add(lblDBType);
        pnlInputFields.add(cbDatabaseType);
        pnlInputFields.add(lblHost);
        pnlInputFields.add(txtHost);
        pnlInputFields.add(lblDBName);
        pnlInputFields.add(txtDBName);
        pnlInputFields.add(lblUsername);
        pnlInputFields.add(txtUsername);
        pnlInputFields.add(lblPassword);
        pnlInputFields.add(txtPassword);
        pnlInputFields.add(lblPort);
        pnlInputFields.add(txtPort);

        dialog.add(pnlInputFields, BorderLayout.CENTER); // Add Input Panel to the Dialog

        // Button Area
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnTest = new JButton("Test Connection");
        JButton btnConnect = new JButton("Connect");
        JButton btnCancel = new JButton("Cancel");

        // Adding Buttons to the Buttons Panel
        pnlButtons.add(btnTest);
        pnlButtons.add(btnConnect);
        pnlButtons.add(btnCancel);
        dialog.add(pnlButtons, BorderLayout.SOUTH);

        // Default Port Suggest
        Map<String, String> dbPorts = Map.of(
                "MySQL", "3306",
                "MSSQL", "1433",
                "OracleDB", "1521",
                "MongoDB", "27017"
        );

        cbDatabaseType.addActionListener(e -> {
            String selectedDbType = (String) cbDatabaseType.getSelectedItem();
            String defaultPort = dbPorts.get(selectedDbType);
            txtPort.setText((defaultPort != null) ? defaultPort : "");
        });
        String selectedDbType = (String) cbDatabaseType.getSelectedItem();
        String defaultPort = dbPorts.get(selectedDbType);
        txtPort.setText((defaultPort != null) ? defaultPort : "");

        // Action listeners for Buttons
        btnConnect.addActionListener(e -> {
            String host = txtHost.getText();
            String dbName = txtDBName.getText();
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());
            String dbType = (String) cbDatabaseType.getSelectedItem();
            String port = txtPort.getText();

            if (host.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            } else {

                if (isDatabaseAlreadyConnected(dbType, host, port)) {
                    JOptionPane.showMessageDialog(dialog,
                            "This database server is already connected!\nServer: " + dbType + "\nHost: " + host + "\nPort: " + port,
                            "Duplicate Connection",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    // Create DatabaseConnector
                    DatabaseConnector dbConnector = new DatabaseConnector(dbType, host, port, dbName, username, password);

                    if (dbConnector.testConnection()) {
                        // Create SQLManager with the new DatabaseConnector
                        SQLManager sqlManager = new SQLManager(dbConnector);

                        // Add to connected databases list
                        addConnectedDatabase(dbConnector);

                        JOptionPane.showMessageDialog(dialog, "Connection Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Connection Failed!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    System.out.println(ex.toString());
                    JOptionPane.showMessageDialog(dialog, "Failed to connect to the specified " + dbType + " server: " + ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCancel.addActionListener(e -> {
            dialog.dispose();
        });

        // Test the connection
        btnTest.addActionListener(e -> {
            String host = txtHost.getText();
            String dbName = txtDBName.getText();
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());
            String dbType = (String) cbDatabaseType.getSelectedItem();
            String port = txtPort.getText();

            if (host.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Host field is required for testing connection!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                DatabaseConnector testConnector = new DatabaseConnector(
                        dbType,
                        host,
                        port,
                        dbName,
                        username,
                        password
                );

                if (testConnector.testConnection()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Test connection successful!\n\nServer: " + dbType
                            + "\nHost: " + host
                            + "\nPort: " + port,
                            "Connection Test",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Test connection failed!\n\nPlease verify your connection details.",
                            "Connection Test Failed",
                            JOptionPane.ERROR_MESSAGE);
                }

                if (testConnector.getConnection() != null) {
                    testConnector.getConnection().close();
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Connection test failed: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    private void executeSQL() {
        String sqlQuery = txtCommandArea.getText().trim();
        if (sqlQuery.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Please enter a SQL query",
                    "Empty Query",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedIndex = dbServerList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Please select a database server",
                    "No Database Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            DatabaseConnector connector = connectedDatabases.get(selectedIndex);
            SQLManager sqlManager = new SQLManager(connector);

            // Execute query and get results
            List<Map<String, Object>> results = sqlManager.executeQuery(sqlQuery);

            // Check if the query is a SELECT statement
            boolean isSelectQuery = sqlQuery.trim().toLowerCase().startsWith("select");

            if (isSelectQuery && results != null && !results.isEmpty()) {
                // Get column names from first row
                String[] columnNames = results.get(0).keySet().toArray(new String[0]);

                // Create data array for JTable
                Object[][] data = new Object[results.size()][columnNames.length];
                for (int i = 0; i < results.size(); i++) {
                    Map<String, Object> row = results.get(i);
                    for (int j = 0; j < columnNames.length; j++) {
                        data[i][j] = row.get(columnNames[j]);
                    }
                }

                // Create and display table
                JTable resultTable = new JTable(data, columnNames);
                JScrollPane scrollPane = new JScrollPane(resultTable);

                // Update viewer panel
                pnlViewer.removeAll();
                pnlViewer.add(scrollPane, BorderLayout.CENTER);
                pnlViewer.revalidate();
                pnlViewer.repaint();
            } else {
                // For non-SELECT queries or empty results
                JOptionPane.showMessageDialog(mainFrame,
                        "Query executed successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Get currently selected node from tree view
                DefaultMutableTreeNode selectedNode
                        = (DefaultMutableTreeNode) databaseTreeManager.getDbTreeView().getLastSelectedPathComponent();

                if (selectedNode != null) {
                    Object userObject = selectedNode.getUserObject();

                    if (userObject instanceof String) {
                        TreeNode parentNode = selectedNode.getParent();
                        if (parentNode instanceof DefaultMutableTreeNode) {
                            Object parentUserObject = ((DefaultMutableTreeNode) parentNode).getUserObject();
                            if (parentUserObject instanceof DatabaseInfo) {
                                DatabaseInfo dbInfo = (DatabaseInfo) parentUserObject;
                                String tableName = (String) userObject;
                                displayTableData(dbInfo, tableName);
                            }
                        }
                    }
                }

//                databaseTreeManager.updateDatabaseTreeView(sqlManager);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Error executing query: " + ex.getMessage(),
                    "SQL Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addConnectedDatabase(DatabaseConnector dbConnector) {
        if (this.connectedDatabases == null) {
            this.connectedDatabases = new ArrayList<>();
        }

        // Create a descriptive server name
        String serverName = String.format("%s - %s:%s",
                dbConnector.getDbServer().toUpperCase(),
                dbConnector.getHost(),
                dbConnector.getPort()
        );

        // Add to list model
        dbServerListModel.addElement(serverName);

        // Add to connected databases list
        connectedDatabases.add(dbConnector);

        // Enable Execute Button
        btnExecuteQuery.setEnabled(true);
        btnRefresh.setEnabled(true);
        miExecute.setEnabled(true);
        btnDisconnectServer.setEnabled(true);
        miDisconnect.setEnabled(true);

        // Optional: Add list selection listener
        dbServerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = dbServerList.getSelectedIndex();
                if (selectedIndex != -1) {
                    DatabaseConnector selected = connectedDatabases.get(selectedIndex);
                    try {
                        // Create a new SQLManager for the selected database connector
                        SQLManager selectedSqlManager = new SQLManager(selected);
                        databaseTreeManager.updateDatabaseTreeView(selectedSqlManager);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(mainFrame,
                                "Error creating SQL Manager: " + ex.getMessage(),
                                "Database Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });
    }

    public void displayTableData(DatabaseInfo dbInfo, String tableName) {
        try {
            // Fetch table data
            SQLManager sqlManager = new SQLManager(dbInfo.getDbConnector());
            String databaseName = dbInfo.getDatabaseName(); // Database Name
            List<Map<String, Object>> tableData = sqlManager.getTableData(databaseName, tableName);

            // Create table model
            List<String> columnNamesList = sqlManager.getTableColumns(databaseName, tableName);
            String[] columnNames = columnNamesList.toArray(new String[0]);

            Object[][] rowData = new Object[tableData.size()][columnNames.length];
            for (int i = 0; i < tableData.size(); i++) {
                Map<String, Object> row = tableData.get(i);
                for (int j = 0; j < columnNames.length; j++) {
                    rowData[i][j] = row.get(columnNames[j]);
                }
            }

            // Display in JTable
            JTable table = new JTable(rowData, columnNames);
            table = TableFormatter.formatTable(table);

            // Create Scroller
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(Color.WHITE);

            // Add a title panel above the table
            JPanel titlePanel = new JPanel(new BorderLayout());
            titlePanel.setBackground(Color.WHITE);
            JLabel titleLabel = new JLabel(" Table: " + tableName);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            titlePanel.add(titleLabel, BorderLayout.WEST);

            // Create main panel to hold title and table
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(titlePanel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            // Update viewer panel
            pnlViewer.removeAll();
            pnlViewer.add(scrollPane, BorderLayout.CENTER);
            pnlViewer.revalidate();
            pnlViewer.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Error displaying table data: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isDatabaseAlreadyConnected(String dbServer, String host, String port) {
        String serverIdentifier = String.format("%s - %s:%s",
                dbServer.toUpperCase(),
                host,
                port
        );

        for (int i = 0; i < dbServerListModel.getSize(); i++) {
            if (dbServerListModel.getElementAt(i).equals(serverIdentifier)) {
                return true;
            }
        }
        return false;
    }

    public void updateFooterStatus(String serverInfo, String databaseName) {
        if (databaseName != null && !databaseName.isEmpty()) {
            lblFooter.setText("Connected to: " + serverInfo + " | Current Database: " + databaseName);
        } else {
            lblFooter.setText("Connected to: " + serverInfo);
        }
    }

    private void disconnectDatabaseServer() {
        // Get selected server index
        int selectedIndex = dbServerList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Please select a database server to disconnect",
                    "No Server Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the selected database connector
        DatabaseConnector selectedConnector = connectedDatabases.get(selectedIndex);
        String serverInfo = String.format("%s - %s:%s",
                selectedConnector.getDbServer().toUpperCase(),
                selectedConnector.getHost(),
                selectedConnector.getPort());

        // Confirm disconnection
        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Are you sure you want to disconnect from:\n" + serverInfo + "?",
                "Confirm Disconnection",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Close the database connection
                if (selectedConnector.getConnection() != null && !selectedConnector.getConnection().isClosed()) {
                    selectedConnector.getConnection().close();
                }

                // Remove from lists
                connectedDatabases.remove(selectedIndex);
                dbServerListModel.remove(selectedIndex);

                // Clear tree view
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) databaseTreeManager.getDbTreeView().getModel().getRoot();
                root.removeAllChildren();
                ((DefaultTreeModel) databaseTreeManager.getDbTreeView().getModel()).reload();

                // Clear viewer panel
                pnlViewer.removeAll();
                pnlViewer.add(lblViewer, BorderLayout.CENTER);
                pnlViewer.revalidate();
                pnlViewer.repaint();

                // Reset footer
                lblFooter.setText("No database selected");

                // Disable Execute Button
                btnExecuteQuery.setEnabled(false);
                miExecute.setEnabled(false);
                btnRefresh.setEnabled(false);

                // Clear command area
                txtCommandArea.setText("");

                // Disable execute button if no more connections
                if (dbServerListModel.isEmpty()) {
                    miExecute.setEnabled(false);
                    for (Component comp : pnlToolBar.getComponents()) {
                        if (comp instanceof JButton && ((JButton) comp).getText().equals("Execute")) {
                            comp.setEnabled(false);
                            break;
                        }
                    }
                }

                JOptionPane.showMessageDialog(mainFrame,
                        "Successfully disconnected from:\n" + serverInfo,
                        "Disconnection Successful",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(mainFrame,
                        "Error disconnecting from database: " + ex.getMessage(),
                        "Disconnection Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void endApplication() {
        int confirm = JOptionPane.showConfirmDialog(
                mainFrame,
                "Are you sure you want to exit?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0); // Terminate the program
        }
    }

    private void showAbout() {
        JDialog aboutDialog = new JDialog(mainFrame, "About", true);
        aboutDialog.setSize(400, 220);
        aboutDialog.setLocationRelativeTo(mainFrame);
        aboutDialog.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Application title
        JLabel titleLabel = new JLabel("Database Management Tool");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Version
        JLabel versionLabel = new JLabel("Assignment 04");
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Developer info
        JLabel developerLabel = new JLabel("Developed by: M. M. S. P. Mapa");
        developerLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        developerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Student ID
        JLabel studentIdLabel = new JLabel("Student ID: SOF/20/B1/03");
        studentIdLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        studentIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components with spacing
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(versionLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(developerLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(studentIdLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // OK button
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> aboutDialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);

        aboutDialog.add(contentPanel, BorderLayout.CENTER);
        aboutDialog.add(buttonPanel, BorderLayout.SOUTH);
        aboutDialog.setVisible(true);
    }

    private void setMaximize() {
        // Toggle between maximized and normal state
        if ((mainFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
            mainFrame.setExtendedState(JFrame.NORMAL);
            miMaximize.setText("Maximize");
        } else {
            mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            miMaximize.setText("Minimize");
        }
    }

    private void refreshDatabasesView() {
        int selectedIndex = dbServerList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Please select a database server to refresh",
                    "No Server Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            DatabaseConnector selectedConnector = connectedDatabases.get(selectedIndex);
            SQLManager sqlManager = new SQLManager(selectedConnector);

            databaseTreeManager.updateDatabaseTreeView(sqlManager);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Error refreshing database view: " + ex.getMessage(),
                    "Refresh Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new MainForm();
    }
}
