# Database Management Tool

A lightweight, Java-based graphical user interface (GUI) application designed to manage and interact with multiple types of relational database management systems (RDBMS).

## 📋 Project Overview

This application provides a unified interface to connect to, explore, and manipulate databases. It allows users to browse database schemas in a tree view, execute custom SQL queries, and view results in a formatted table.

**Developer:** Sandun Prasanna

## ✨ Key Features

* **Multi-Database Support:** Connect to **MySQL**, **MSSQL (SQL Server)**, and **OracleDB**.
* **Connection Management:**
    * Test connections before connecting.
    * Manage multiple active server connections simultaneously.
    * Status bar indicating current connection and active database.
* **Schema Explorer (Tree View):**
    * Visualize Servers, Databases, Tables, and Columns in a hierarchical tree structure.
    * **Context Menus:** Right-click on nodes to perform actions like:
        * Create/Delete Databases.
        * Rename/Delete Tables.
        * Refresh Database/Table views.
* **SQL Execution:**
    * Dedicated command area to write and execute SQL queries (`SELECT`, `INSERT`, `UPDATE`, `DELETE`).
    * Automatic results rendering in the viewer area.
* **Data Viewer:**
    * View table data in a formatted grid.
    * Auto-formatted headers and row colors (alternating rows, selection highlighting).
    * Smart column sizing based on content width.

## 🛠 Technical Stack

* **Language:** Java (JDK 1.8 / 17 compatible)
* **GUI Framework:** Java Swing
* **Build System:** Apache Ant (NetBeans Project)
* **Database Connectivity:** JDBC (Java Database Connectivity)

## 📦 Dependencies

To run this project, you must have the following JDBC drivers added to your classpath/library:

1.  **MySQL:** `mysql-connector-j-9.1.0.jar` (or compatible version)
2.  **Oracle:** `ojdbc17.jar` (or compatible version)
3.  **MSSQL:** `mssql-jdbc-12.8.1.jre11.jar` (or `jre8` version)

## 🚀 Installation & Setup

### Prerequisites
* Java Development Kit (JDK) installed on your system.
* NetBeans IDE (recommended) or any Java IDE.
* Local or remote instances of MySQL, MSSQL, or OracleDB running.

### Steps to Run

1.  **Clone/Download the Repository**
    Extract the project files to your local machine.

2.  **Open in IDE**
    * Open NetBeans IDE.
    * Go to `File` -> `Open Project` and select the project folder.

3.  **Configure Libraries**
    * Right-click the project in the "Projects" view.
    * Select `Properties` -> `Libraries`.
    * Ensure the JAR files listed in the **Dependencies** section above are added to the Compile-time libraries. *Note: The project configuration currently points to local paths (e.g., `../../../Downloads/...`) which need to be updated to match your system's file structure*.

4.  **Build and Run**
    * Clean and Build the project.
    * Run `assignment.MainForm` as the main class.

## 📖 Usage Guide

1.  **Connect to a Server:**
    * Click the **Connect** button on the toolbar or select `File > Connect`.
    * Select the Database Type (MySQL, MSSQL, OracleDB).
    * Enter Host, Port, Username, and Password.
    * Click **Test Connection** to verify, then **Connect**.

2.  **Browse Data:**
    * Expand the server node in the left-hand "Explore Area".
    * Click on a database to load its tables.
    * Click on a table to view its data in the central "Viewer Area".

3.  **Execute SQL:**
    * Type your SQL query in the bottom "Command Area".
    * Click the **Execute** button (Play icon).
    * Results (or success/error messages) will appear in the Viewer Area or via popups.

4.  **Manage Objects:**
    * **Right-click** a Database node to delete it or create a new table inside it.
    * **Right-click** a Table node to Rename, Delete, or Refresh it.

## 📂 Project Structure

* `src/assignment/`
    * `MainForm.java`: Main application window and UI logic.
    * `DatabaseConnector.java`: Handles JDBC driver loading and connections.
    * `DatabaseTreeManager.java`: Manages the schema tree view and context menus.
    * `SQLManager.java`: Handles SQL query execution and result set processing.
    * `TableFormatter.java`: Utilities for styling the JTable.
    * `CustomComponents.java`: Helper class for UI components and icons.
* `src/icons/`: Contains UI assets (PNG icons for buttons and tree nodes).

## 📜 License

This project is for academic assignment purposes.

---
*Generated based on source code analysis.*
