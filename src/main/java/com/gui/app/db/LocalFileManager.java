package com.gui.app.db;


import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDateTime;

public class LocalFileManager {

    private static final String LOCAL_DIR = System.getProperty("user.home") + "/my-cloud-sync/";
    private static final String DB_URL = "jdbc:sqlite:comp20081.db";
    static {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS local_files (\n" +
                "    filename TEXT PRIMARY KEY,\n" +
                "    path TEXT NOT NULL,\n" +
                "    action TEXT NOT NULL,          -- create, update, delete\n" +
                "    username TEXT NOT NULL,\n" +
                "    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP\n" +
                ");"
               );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    static {
        new File("tempfiles").mkdirs();
    }

    public static boolean saveFileLocally(String filename, String content, String username, String action) {
        try {
            Path filePath = Paths.get("tempfiles", filename);
            Files.writeString(filePath, content);

            logToSQLite(filename, filePath.toString(), username, action);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteFileLocally(String filename, String username) {
        try {
            Path filePath = Paths.get("tempfiles", filename);
            Files.deleteIfExists(filePath);

            logToSQLite(filename, filePath.toString(), username, "delete");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void logToSQLite(String filename, String path, String username, String action) {
        String sql = "REPLACE INTO local_files (filename, path, username, action, timestamp) VALUES (?, ?, ?, ?, datetime('now'))";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, filename);
            stmt.setString(2, path);
            stmt.setString(3, username);
            stmt.setString(4, action);
            stmt.executeUpdate();
            System.out.println("Data saved to db for user "+ username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
