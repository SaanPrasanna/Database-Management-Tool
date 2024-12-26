package assignment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 *
 * @author SOF/20/B1/03 - Sandun
 */
public class DatabaseConnector {

    private String driver;
    private String dbServer;
    private String host;
    private String port;
    private String dbName;
    private String dbUser;
    private String dbUserPassword;
    private Connection connection;

    public DatabaseConnector(String dbServer, String host, String port, String dbName, String dbUser, String dbUserPassword) throws ClassNotFoundException, SQLException {
        this.dbServer = dbServer;
        this.host = host;
        this.port = port;
        this.dbName = null;
        this.dbUser = dbUser;
        this.dbUserPassword = dbUserPassword;
        loadDriver();
        this.connection = DriverManager.getConnection(getJDBCURL(), this.dbUser, this.dbUserPassword);
    }

    private void loadDriver() throws ClassNotFoundException {
        switch (this.dbServer.toLowerCase()) {
            case "mysql":
                Class.forName("com.mysql.cj.jdbc.Driver");
                break;
            case "mssql":
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                break;
            case "oracle":
                Class.forName("oracle.jdbc.OracleDriver");
                break;
            default:
                throw new ClassNotFoundException("Unsupported database type: " + this.dbServer);
        }
    }

    private String getJDBCURL() {
        String jdbcURL = null;
        switch (this.dbServer.toLowerCase()) {
            case "mysql":
                jdbcURL = "jdbc:mysql://" + this.host + ":" + this.port;
                /* + "/" + this.dbName;*/
                break;
            case "mssql":
                jdbcURL = "jdbc:sqlserver://" + this.host + ":" + this.port
                        + ";encrypt=true;trustServerCertificate=true";
                break;
            case "oracle":
                jdbcURL = "jdbc:oracle:thin:@" + this.host + ":" + this.port;
                /*+ ":" + this.dbName;*/
                break;
            default:
                throw new IllegalArgumentException("Unsupported database type: " + this.dbServer);
        }
        return jdbcURL;
    }

    public void listAllDatabases() throws SQLException {
        String query = null;
        switch (this.dbServer.toLowerCase()) {
            case "mysql":
                query = "SHOW DATABASES";
                break;
            case "mssql":
                query = "SELECT name FROM sys.databases";
                break;
            case "oracle":
                query = "SELECT name FROM v$database";
                break;
            default:
                throw new SQLException("Unsupported database type for listing databases: " + this.dbServer);
        }

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        System.out.println("Databases available on " + this.dbServer + " server:");
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
    }

    public boolean testConnection() {
        Connection connection = null;
        try {
            String url = getJDBCURL();

            connection = DriverManager.getConnection(url, dbUser, dbUserPassword);
            return connection != null;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDbServer() {
        return dbServer;
    }

    public void setDbServer(String dbServer) {
        this.dbServer = dbServer;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbUserPassword() {
        return dbUserPassword;
    }

    public void setDbUserPassword(String dbUserPassword) {
        this.dbUserPassword = dbUserPassword;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
