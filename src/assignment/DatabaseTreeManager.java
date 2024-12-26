/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignment;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.util.logging.*;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author SOF/20/B1/03 - Sandun
 */
public class DatabaseTreeManager {

    private JTree dbTreeView;
    private DefaultTreeModel treeModel;
    private JPanel pnlExploreArea2;
    private JFrame mainFrame;
    private MainForm mainForm;
    private SQLManager currentSQLManager;
    private ImageIcon serverIcon, databaseIcon, tableIcon, columnIcon;

    public DatabaseTreeManager(JPanel pnlExploreArea2, MainForm mainForm) {
        this.pnlExploreArea2 = pnlExploreArea2;
        this.mainForm = mainForm;
        initializeTreeView();

        loadIcons();

        // Rendering nodes to insert the icons
        dbTreeView.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value,
                    boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();

                if (userObject instanceof DatabaseInfo) {
                    setIcon(databaseIcon);
                } else if (userObject instanceof String) {
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                    if (parent != null) {
                        Object parentObject = parent.getUserObject();
                        if (parentObject instanceof String && parent.getParent() != null
                                && ((DefaultMutableTreeNode) parent.getParent()).getUserObject() instanceof DatabaseInfo) {
                            // Column Node
                            setIcon(columnIcon);
                        } else if (parentObject instanceof DatabaseInfo) {
                            // Table node
                            setIcon(tableIcon);
                        } else if (parent == tree.getModel().getRoot()) {
                            // Server node
                            setIcon(serverIcon);
                        }
                    }
                }

                return this;
            }
        });

        // Handling listener for tree view context menu
        dbTreeView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = dbTreeView.getRowForLocation(e.getX(), e.getY());
                    if (row != -1) {
                        dbTreeView.setSelectionRow(row);
                        TreePath path = dbTreeView.getPathForRow(row);
                        if (path != null) {
                            Object selectedNode = path.getLastPathComponent();
                            if (selectedNode instanceof DefaultMutableTreeNode) {
                                DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedNode;
                                showContextMenu(node, e.getX(), e.getY());
                            }
                        }
                    }
                }
            }
        });
    }

    private void loadIcons() {
        databaseIcon = CustomComponents.scaleIcon(new ImageIcon(getClass().getResource("/icons/database.png")), 16, 16);
        tableIcon = CustomComponents.scaleIcon(new ImageIcon(getClass().getResource("/icons/table.png")), 16, 16);
        serverIcon = CustomComponents.scaleIcon(new ImageIcon(getClass().getResource("/icons/server.png")), 16, 16);
        columnIcon = CustomComponents.scaleIcon(new ImageIcon(getClass().getResource("/icons/column.png")), 16, 16);
    }

    private void showContextMenu(DefaultMutableTreeNode node, int x, int y) {
        JPopupMenu popupMenu = new JPopupMenu();

        Object userObject = node.getUserObject();

        if (userObject instanceof String) {
            JMenuItem refreshTable = new JMenuItem("Refresh Table");
            JMenuItem renameItem = new JMenuItem("Rename Table");
            JMenuItem deleteItem = new JMenuItem("Delete Table");

            // Table Context Buttons Actions
            refreshTable.addActionListener(e -> refreshTable(node));
            renameItem.addActionListener(e -> manageTable(node, "rename"));
            deleteItem.addActionListener(e -> manageTable(node, "delete"));

            popupMenu.add(refreshTable);
            popupMenu.add(renameItem);
            popupMenu.add(deleteItem);
        } else if (userObject instanceof DatabaseInfo) {
            JMenuItem createTableItem = new JMenuItem("Create Table");
            JMenuItem refreshDatabase = new JMenuItem("Refresh Database");
            JMenuItem deleteItem = new JMenuItem("Delete Database");

            // Database Context Actions | I will require souded by Try Catch; because method using exceptions
            createTableItem.addActionListener(e -> {
                try {
                    manageDatabase(node, "create");
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseTreeManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            refreshDatabase.addActionListener(e -> refreshDatabase(node));
            deleteItem.addActionListener(e -> {
                try {
                    manageDatabase(node, "delete");
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseTreeManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            popupMenu.add(createTableItem);
            popupMenu.add(refreshDatabase);
            popupMenu.add(deleteItem);
        }

        popupMenu.show(dbTreeView, x, y);
    }
    
    // Shared button to hanldle 1. Delete 2.Rename
    private void manageTable(DefaultMutableTreeNode node, String action) {
        Object userObject = node.getUserObject();

        if (userObject instanceof String) {
            String tableName = (String) userObject;

            if ("delete".equalsIgnoreCase(action)) {
                // Handle delete action
                int confirmation = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to delete the table: " + tableName + "?",
                        "Delete Table", JOptionPane.YES_NO_OPTION);

                if (confirmation == JOptionPane.YES_OPTION) {
                    try {
                        currentSQLManager.manageTable(tableName, null, "delete");

                        JOptionPane.showMessageDialog(null,
                                "Table " + tableName + " deleted successfully.",
                                "Success", JOptionPane.INFORMATION_MESSAGE);

                        // Refersh Tree
                        updateDatabaseTreeView(currentSQLManager);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null,
                                "Error deleting table: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else if ("rename".equalsIgnoreCase(action)) {
                // Handle rename action
                String newTableName = JOptionPane.showInputDialog("Enter new table name for " + tableName + ":");

                if (newTableName != null && !newTableName.trim().isEmpty()) {
                    try {
                        currentSQLManager.manageTable(tableName, newTableName, "rename");

                        JOptionPane.showMessageDialog(null,
                                "Table " + tableName + " renamed to " + newTableName + " successfully.",
                                "Success", JOptionPane.INFORMATION_MESSAGE);

                        // Refresh
                        updateDatabaseTreeView(currentSQLManager);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null,
                                "Error renaming table: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Invalid table name provided.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "Invalid action specified.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "The selected node is not a table.",
                    "Invalid Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    // for the database context menu 1. Delete db 2. Create table
    private void manageDatabase(DefaultMutableTreeNode node, String action) throws SQLException {
        Object userObject = node.getUserObject();

        if (userObject instanceof DatabaseInfo) {
            DatabaseInfo dbInfo = (DatabaseInfo) userObject;
            String databaseName = dbInfo.getDatabaseName();

            if ("delete".equalsIgnoreCase(action)) {

                int confirmation = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to delete the database: " + databaseName + "?",
                        "Delete Database", JOptionPane.YES_NO_OPTION);

                if (confirmation == JOptionPane.YES_OPTION) {
                    try {
                        currentSQLManager.deleteDatabase(databaseName);

                        JOptionPane.showMessageDialog(null,
                                "Database " + databaseName + " deleted successfully.",
                                "Success", JOptionPane.INFORMATION_MESSAGE);

                        updateDatabaseTreeView(currentSQLManager);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null,
                                "Error deleting database: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else if ("create".equalsIgnoreCase(action)) {
                int confirmation = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to create a table in " + databaseName + " ?",
                        "Create Table", JOptionPane.YES_NO_OPTION);

                if (confirmation == JOptionPane.YES_OPTION) {
                    try {
                        currentSQLManager.getDc().getConnection().setCatalog(databaseName);
                        mainForm.txtCommandArea.requestFocus();
                        mainForm.txtCommandArea.setText(
                                "CREATE TABLE table_name (\n"
                                + "    column1 INT AUTO_INCREMENT,\n"
                                + "    column2 datatype,\n"
                                + "    column3 datatype,\n"
                                + "    PRIMARY KEY (column1)\n"
                                + ");"
                        );
                    } catch (SQLException ex) {

                    }

                }
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "The selected node is not a database.",
                    "Invalid Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    // For Refresh context menu button | Table context menu
    private void refreshTable(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof String) {
            String tableName = (String) userObject;
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();

            if (parentNode != null && parentNode.getUserObject() instanceof DatabaseInfo) {
                DatabaseInfo dbInfo = (DatabaseInfo) parentNode.getUserObject();
                try {
                    currentSQLManager.getDc().getConnection().setCatalog(dbInfo.getDatabaseName());

                    mainForm.displayTableData(dbInfo, tableName);

                    JOptionPane.showMessageDialog(null,
                            "Table " + tableName + " refreshed successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,
                            "Error refreshing table: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(DatabaseTreeManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    // For the refresh context menu button | Database context menu
    private void refreshDatabase(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof DatabaseInfo) {
            DatabaseInfo dbInfo = (DatabaseInfo) userObject;
            try {
                node.removeAllChildren();

                treeModel.reload(node);

                loadTablesForDatabase(dbInfo, currentSQLManager);

                JOptionPane.showMessageDialog(null,
                        "Database " + dbInfo.getDatabaseName() + " refreshed successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        "Error refreshing database: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(DatabaseTreeManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void initializeTreeView() {
        DefaultMutableTreeNode hiddenRoot = new DefaultMutableTreeNode();
        treeModel = new DefaultTreeModel(hiddenRoot);
        dbTreeView = new JTree(treeModel);
        dbTreeView.setRootVisible(false);
        dbTreeView.setShowsRootHandles(true);

        // Tree selection listener
        dbTreeView.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode
                    = (DefaultMutableTreeNode) dbTreeView.getLastSelectedPathComponent();

            if (selectedNode != null) {
                try {
                    handleTreeNodeSelection(selectedNode);
                } catch (SQLException ex) {
                    Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        // Scroller
        JScrollPane scrollPane = new JScrollPane(dbTreeView);
        pnlExploreArea2.removeAll();
        pnlExploreArea2.add(scrollPane, BorderLayout.CENTER);
        pnlExploreArea2.revalidate();
        pnlExploreArea2.repaint();
    }

    public void updateDatabaseTreeView(SQLManager sqlManager) {
        try {
            this.currentSQLManager = sqlManager;

            // Reset
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
            root.removeAllChildren();

            // Get databases | Selected db server
            List<String> databases = sqlManager.getDatabaseList();

            DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode(
                    String.format("%s - %s:%s",
                            sqlManager.getDc().getDbServer().toUpperCase(),
                            sqlManager.getDc().getHost(),
                            sqlManager.getDc().getPort()
                    )
            );

            String serverInfo = String.format("%s - %s:%s",
                    sqlManager.getDc().getDbServer().toUpperCase(),
                    sqlManager.getDc().getHost(),
                    sqlManager.getDc().getPort()
            );
            mainForm.updateFooterStatus(serverInfo, null);

            for (String dbName : databases) {
                DatabaseInfo dbInfo = new DatabaseInfo(sqlManager.getDc(), dbName);
                DefaultMutableTreeNode dbNode = new DefaultMutableTreeNode(dbInfo);
                dbNode.add(new DefaultMutableTreeNode("Loading..."));
                serverNode.add(dbNode);
            }

            root.add(serverNode);

            treeModel.reload(root);

            configureTreeExpansionListener();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame,
                    "Error loading databases: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void configureTreeExpansionListener() {
        dbTreeView.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                DefaultMutableTreeNode node
                        = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();

                if (node.getUserObject() instanceof DatabaseInfo) {
                    // Database node found
                    try {
                        node.removeAllChildren();

                        DatabaseInfo dbInfo = (DatabaseInfo) node.getUserObject();
                        loadTablesForDatabase(dbInfo, currentSQLManager);
                    } catch (SQLException ex) {
                        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(mainFrame,
                                "Error loading tables: " + ex.getMessage(),
                                "Database Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else if (node.getUserObject() instanceof String) {
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                    if (parent != null && parent.getUserObject() instanceof DatabaseInfo) {
                        // table node found
                        try {
                            node.removeAllChildren();
                            DatabaseInfo dbInfo = (DatabaseInfo) parent.getUserObject();
                            String tableName = (String) node.getUserObject();
                            loadColumnsForTable(dbInfo, tableName, node);
                        } catch (SQLException ex) {
                            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(mainFrame,
                                    "Error loading columns: " + ex.getMessage(),
                                    "Database Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
            }
        });
    }

    private void handleTreeNodeSelection(DefaultMutableTreeNode selectedNode) throws SQLException {
        Object userObject = selectedNode.getUserObject();

        if (userObject instanceof String) {
            String tableName = (String) userObject;
            TreeNode parentNode = selectedNode.getParent();
            if (parentNode instanceof DefaultMutableTreeNode) {
                // Table node found
                Object parentUserObject = ((DefaultMutableTreeNode) parentNode).getUserObject();
                if (parentUserObject instanceof DatabaseInfo) {
                    DatabaseInfo dbInfo = (DatabaseInfo) parentUserObject;
                    
                    String serverInfo = String.format("%s - %s:%s",
                            dbInfo.getDbConnector().getDbServer().toUpperCase(),
                            dbInfo.getDbConnector().getHost(),
                            dbInfo.getDbConnector().getPort()
                    );
                    mainForm.updateFooterStatus(serverInfo, dbInfo.getDatabaseName());
                    mainForm.displayTableData(dbInfo, tableName);
                }
            }
        } else if (userObject instanceof DatabaseInfo) {
            DatabaseInfo dbInfo = (DatabaseInfo) userObject;
            String serverInfo = String.format("%s - %s:%s",
                    dbInfo.getDbConnector().getDbServer().toUpperCase(),
                    dbInfo.getDbConnector().getHost(),
                    dbInfo.getDbConnector().getPort()
            );
            mainForm.updateFooterStatus(serverInfo, dbInfo.getDatabaseName());
        }
    }

    private void loadTablesForDatabase(DatabaseInfo dbInfo, SQLManager sqlManager) throws SQLException {
        List<String> tables = sqlManager.getTables(dbInfo.getDatabaseName());

        // Find the database node
        DefaultMutableTreeNode dbNode = findNodeByUserObject(dbInfo);
        if (dbNode != null) {
            // Add tables to database node
            for (String tableName : tables) {
                DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(tableName);
//                dbNode.add(new DefaultMutableTreeNode(tableName));

                tableNode.add(new DefaultMutableTreeNode("Loading..."));
                dbNode.add(tableNode);
            }

            treeModel.reload(dbNode);
        }
    }

    private void loadColumnsForTable(DatabaseInfo dbInfo, String tableName, DefaultMutableTreeNode tableNode)
            throws SQLException {
        List<String> columns = currentSQLManager.getTableColumns(dbInfo.getDatabaseName(), tableName);

        for (String column : columns) {
            DefaultMutableTreeNode columnNode = new DefaultMutableTreeNode(column);
            tableNode.add(columnNode);
        }

        treeModel.reload(tableNode);
    }

    private DefaultMutableTreeNode findNodeByUserObject(Object userObject) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        return findNodeRecursively(root, userObject);
    }

    private DefaultMutableTreeNode findNodeRecursively(DefaultMutableTreeNode node, Object userObject) {
        if (node.getUserObject() != null && node.getUserObject().equals(userObject)) {
            return node;
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            DefaultMutableTreeNode found = findNodeRecursively(child, userObject);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    public JTree getDbTreeView() {
        return dbTreeView;
    }

}

// To store the database data
class DatabaseInfo {

    private final DatabaseConnector dbConnector;
    private final String databaseName;

    public DatabaseInfo(DatabaseConnector dbConnector, String databaseName) {
        this.dbConnector = dbConnector;
        this.databaseName = databaseName;
    }

    public DatabaseConnector getDbConnector() {
        return dbConnector;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public String toString() {
        return databaseName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DatabaseInfo that = (DatabaseInfo) obj;
        return databaseName.equals(that.databaseName)
                && dbConnector == that.dbConnector;
    }
}
