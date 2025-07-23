package com.gui.app.util;


import java.sql.*;
import java.util.*;
import com.gui.app.model.*;

public class SQLiteHelper {
    private static final String DB_URL = "jdbc:sqlite:users.db";

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
        String sql = "INSERT INTO users(username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password); // Should hash in production
            pstmt.setString(3, role);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Register Error: " + e.getMessage());
            return false;
        }
    }

    public static User validateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password); // Should hash and compare
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
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
}
