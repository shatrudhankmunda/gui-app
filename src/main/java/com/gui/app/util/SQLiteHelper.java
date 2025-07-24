package com.gui.app.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;
import java.util.*;
import com.gui.app.model.*;
import java.security.NoSuchAlgorithmException;

public class SQLiteHelper {
    private static final String DB_URL = "jdbc:sqlite:comp20081.db";
    static {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            stmt.execute(
             "CREATE TABLE IF NOT EXISTS users (" +
             "id INTEGER PRIMARY KEY AUTOINCREMENT," +
             "username TEXT UNIQUE NOT NULL," +
             "password TEXT NOT NULL," +
                "role TEXT NOT NULL DEFAULT 'standard'" +
                ")"
               );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    public static boolean registerUser(String username, String password, String role) {
    String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
    String insertSql = "INSERT INTO users(username, password, role) VALUES (?, ?, ?)";

    try (Connection conn = DriverManager.getConnection(DB_URL)) {

        // Check if user exists
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.err.println("User already exists.");
                return false;
            }
        }

        // Insert user
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setString(1, username);
            insertStmt.setString(2, hashPassword(password)); // secure version
            insertStmt.setString(3, role);
            insertStmt.executeUpdate();
            return true;
        }

    } catch (SQLException e) {
        System.err.println("Register Error: " + e.getMessage());
        return false;
    }
}

    public static User validateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                String storedHash = rs.getString("password");
                if(storedHash.equals(hashPassword(password)))
                return new User(rs.getString("username"), rs.getString("role"));
                }
        } catch (SQLException e) {
            System.err.println("Login Error: " + e.getMessage());
            return null;
        }
      return null;
    }
    public static String getUserRole(String username) {
        String sql = "SELECT role FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            } else {
                return "standard"; // default fallback
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user role: " + e.getMessage());
            return "standard"; // fallback on error
        }
    }
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username, role FROM users")) {
            while (rs.next()) {
                users.add(new User(rs.getString("username"), rs.getString("role")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

      public static List<String> getAllUsername() {
        List<String> users = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username FROM users")) {
            while (rs.next()) {
                users.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

  public static String hashPassword(String password) {
    try {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
    }
}

}
