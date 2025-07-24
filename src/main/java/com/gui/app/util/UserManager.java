package com.gui.app.util;


import com.gui.app.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.gui.app.util.SQLiteHelper;

public class UserManager {
    private static final String DB_URL = "jdbc:sqlite:comp20081.db";
    private static final String USER = "root";
    private static final String PASS = "rootpass";

    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try{
           users = SQLiteHelper.getAllUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public static void updateUserRole(String username, String newRole) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET role = ? WHERE username = ?")) {
            pstmt.setString(1, newRole);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteUser(String username) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE username = ?")) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
