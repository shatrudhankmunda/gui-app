package com.gui.app.util;


import java.sql.*;

public class ACLUtils {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/cloudstore";
    private static final String USER = "root";
    private static final String PASS = "rootpass";

    public static void shareFile(String owner, String file, String targetUser, String permission) throws SQLException {
        String sql = "INSERT INTO file_acl (owner, file_name, shared_with, permission) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, owner);
            pstmt.setString(2, file);
            pstmt.setString(3, targetUser);
            pstmt.setString(4, permission);
            pstmt.executeUpdate();
        }
    }

    public static String getPermission(String requester, String file, String owner) throws SQLException {
        if (requester.equals(owner)) return "write";

        String sql = "SELECT permission FROM file_acl WHERE owner = ? AND file_name = ? AND shared_with = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, owner);
            pstmt.setString(2, file);
            pstmt.setString(3, requester);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("permission");
            }
        }
        return "none";
    }
}
