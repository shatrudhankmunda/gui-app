package com.gui.app.remote;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;

public class RemoteDatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/cloudstore_remote";
    private static final String USER = "clouduser";
    private static final String PASSWORD = "cloudpass";


    public static Connection getConnection() throws SQLException {
    try {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Connected to MySQL!");
        return conn;
    } catch (SQLException e) {
        System.err.println("MySQL Connection Error: " + e.getMessage());
        throw e;  // rethrow so caller can handle it
    }
}
static {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                createSessionTable(conn);
                createFileMetadataTable(conn);
                System.out.println("MySQL tables ensured (created if not exists).");
            }
        } catch (SQLException e) {
            System.err.println("MySQL Initialization Error: " + e.getMessage());
        }
    }
private static void createSessionTable(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS session (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(100) NOT NULL,
                login_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                logout_time DATETIME
            );
        """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

private static void createFileMetadataTable(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS file_metadata (
                filename VARCHAR(255) PRIMARY KEY,
                path TEXT,
                status ENUM('created', 'updated', 'deleted'),
                owner VARCHAR(100),
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            );
        """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

}
