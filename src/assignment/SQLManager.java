/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignment;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author SOF/20/B1/03 - Sandun
 */
public class SQLManager {

    private DatabaseConnector dc;
    private Statement stmt;
    private ResultSet resultset;
    private String query;
    private JTable dataTable;
    private int executed;

    public SQLManager(DatabaseConnector dc) throws SQLException {
        this.dc = dc;
        this.query = "";
        this.stmt = null;
        this.resultset = null;
        this.executed = 0;
        this.dataTable = new JTable();
    }

    public DatabaseConnector getDc() {
        return dc;
    }

    public void setDc(DatabaseConnector dc) {
        this.dc = dc;
    }

    public Statement getStmt() {
        return stmt;
    }

    public void setStmt(Statement stmt) {
        this.stmt = stmt;
    }

    public ResultSet getResultset() {
        return resultset;
    }

    public void setResultset(ResultSet resultset) {
        this.resultset = resultset;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int isExecuted() {
        return executed;
    }

    public void setExecuted(int executed) {
        this.executed = executed;
    }

    public JTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(JTable dataTable) {
        this.dataTable = dataTable;
    }

    public void execute() throws SQLException {
        if (!this.query.equals("")) {
            this.stmt = this.dc.getConnection().createStatement();
            String queryPieces[] = this.query.split(" ");
            String firstWord = queryPieces[0].toUpperCase();
            // System.out.println("First Word: " + firstWord);

            if (firstWord.equals("SELECT") || firstWord.equals("SHOW") || firstWord.equals("DESCRIBE")) {
                this.resultset = this.stmt.executeQuery(this.query);
                this.executed = 1;
                convertResultSetToJTable();
            }

            if (firstWord.equals("INSERT") || firstWord.equals("UPDATE") || firstWord.equals("DELETE")) {
                this.executed = this.stmt.executeUpdate(this.query);
            }

        }
    }

    private void convertResultSetToJTable() throws SQLException {
        DefaultTableModel tableModel = new DefaultTableModel();
        ResultSetMetaData metaData = this.resultset.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
            tableModel.addColumn(metaData.getColumnLabel(columnIndex));
        }
        Object[] row = new Object[columnCount];
        while (this.resultset.next()) {
            for (int i = 0; i < columnCount; i++) {
                row[i] = this.resultset.getObject(i + 1);
            }
            tableModel.addRow(row);
        }

        this.dataTable.setModel(tableModel);
    }

    // Get Databases
    public List<String> getDatabaseList() throws SQLException {
        List<String> databases = new ArrayList<>();
        Connection conn = null;
        ResultSet rs = null;

        try {
            conn = dc.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();

            switch (dc.getDbServer().toLowerCase()) {
                case "mysql":
                    rs = conn.createStatement().executeQuery("SHOW DATABASES");
                    while (rs.next()) {
                        databases.add(rs.getString(1));
                    }
                    break;
                case "mssql":
                    rs = conn.createStatement().executeQuery("SELECT name FROM sys.databases");
                    while (rs.next()) {
                        databases.add(rs.getString(1));
                    }
                    break;
                case "oracle":
                    rs = metaData.getCatalogs();
                    while (rs.next()) {
                        databases.add(rs.getString(1));
                    }
                    break;
                default:
                    throw new SQLException("Unsupported database type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }

        return databases;
    }

    // Get Tables
    public List<String> getTables(String databaseName) throws SQLException {
        List<String> tables = new ArrayList<>();
        Connection conn = null;
        ResultSet rs = null;

        try {
            conn = dc.getConnection();
            if (dc.getDbServer().toLowerCase().equals("mysql")
                    || dc.getDbServer().toLowerCase().equals("mssql")) {
                conn.setCatalog(databaseName);
            }

            DatabaseMetaData metaData = conn.getMetaData();

            switch (dc.getDbServer().toLowerCase()) {
                case "mysql":
                    rs = metaData.getTables(databaseName, null, "%", new String[]{"TABLE"});
                    break;
                case "mssql":
                    rs = metaData.getTables(databaseName, "dbo", "%", new String[]{"TABLE"});
                    break;
                case "oracle":
                    rs = metaData.getTables(null, null, "%", new String[]{"TABLE"});
                    break;
                default:
                    throw new SQLException("Unsupported database type");
            }

            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }

        return tables;
    }

    // Get Table Columns
    public List<String> getTableColumns(String databaseName, String tableName) throws SQLException {
        List<String> columns = new ArrayList<>();
        Connection conn = null;
        ResultSet rs = null;

        try {
            conn = dc.getConnection();
            conn.setCatalog(databaseName);

            DatabaseMetaData metaData = conn.getMetaData();

            rs = metaData.getColumns(databaseName, null, tableName, "%");

            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }

        return columns;
    }

    // Get table data - Retrieve the data
    public List<Map<String, Object>> getTableData(String databaseName, String tableName) throws SQLException {
        List<Map<String, Object>> tableData = new ArrayList<>();
        Connection conn = null;
        ResultSet rs = null;

        try {
            conn = dc.getConnection();
            conn.setCatalog(databaseName);

            String query = "SELECT * FROM " + tableName;
            rs = conn.createStatement().executeQuery(query);

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new java.util.HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnLabel(i), rs.getObject(i));
                }
                tableData.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }

        return tableData;
    }

    // Execute Query then main form button pressed
    public List<Map<String, Object>> executeQuery(String query) throws SQLException {
        System.out.println("Starting executeQuery method...");

        List<Map<String, Object>> results = new ArrayList<>();
        System.out.println("Query to be executed: " + query);

        Connection conn = this.dc.getConnection();
        if (conn == null || conn.isClosed()) {
            throw new SQLException("Cannot execute query: connection is closed or not available.");
        }

        try (Statement stmt = conn.createStatement()) {
            boolean hasResultSet = stmt.execute(query);

            if (hasResultSet) {
                // for SELECT queries
                try (ResultSet rs = stmt.getResultSet()) {
                    System.out.println("Database connection is active.");
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    System.out.println("Number of columns: " + columnCount);

                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnLabel(i);
                            Object value = rs.getObject(i);
                            row.put(columnName, value);
                        }
                        results.add(row);
                    }
                    System.out.println("Query executed successfully. Rows fetched: " + results.size());
                }
            } else {
                // non-SELECT queries
                int updateCount = stmt.getUpdateCount();
                System.out.println("Query executed successfully. Rows affected: " + updateCount);
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception occurred: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return results;
    }

    // Combined function to Delete and Rename the table
    public void manageTable(String tableName, String newTableName, String action) throws SQLException {
        try (Statement stmt = dc.getConnection().createStatement()) {
            if ("delete".equalsIgnoreCase(action)) {
                String query = "DROP TABLE " + tableName;
                stmt.executeUpdate(query);
                System.out.println("Table " + tableName + " deleted successfully.");
            } else if ("rename".equalsIgnoreCase(action)) {
                if (newTableName == null || newTableName.isEmpty()) {
                    throw new IllegalArgumentException("New table name must be provided for renaming.");
                }
                String query = "ALTER TABLE " + tableName + " RENAME TO " + newTableName;
                stmt.executeUpdate(query);
                System.out.println("Table " + tableName + " renamed to " + newTableName + " successfully.");
            } else {
                throw new IllegalArgumentException("Invalid action. Please specify 'rename' or 'delete'.");
            }
        } catch (SQLException e) {
            throw e; 
        }
    }

    public void deleteDatabase(String databaseName) throws SQLException {
        try (Statement stmt = dc.getConnection().createStatement()) {
            String query = "DROP DATABASE IF EXISTS " + databaseName;
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            throw e;
        }
    }
}
