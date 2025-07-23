package com.gui.app.util;


import com.gui.app.model.AclEntry;
import java.sql.*;
import java.util.*;
import com.gui.app.session.*;

public class AclManager {

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:user.db");
    }

    static {
        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS file_acl (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " filename TEXT NOT NULL," +
                " owner TEXT NOT NULL," +
                " shared_user TEXT NOT NULL," +
                " can_read INTEGER DEFAULT 0," +
                " can_write INTEGER DEFAULT 0" +
                ")"
               );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void grantAccess(AclEntry entry) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO file_acl (filename, owner, shared_user, can_read, can_write) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, entry.getFilename());
            ps.setString(2, entry.getOwner());
            ps.setString(3, entry.getSharedUser());
            ps.setInt(4, entry.isCanRead() ? 1 : 0);
            ps.setInt(5, entry.isCanWrite() ? 1 : 0);
            ps.executeUpdate();
        }
    }

    public static boolean canRead(String filename, String username) throws SQLException {     
         return checkAccess(filename, username, true);
    }

    public static boolean canWrite(String filename, String username) throws SQLException {
        return checkAccess(filename, username, false);
    }

    private static boolean checkAccess(String filename, String user, boolean read) throws SQLException {
        String column = read ? "can_read" : "can_write";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT 1 FROM file_acl WHERE filename = ? AND shared_user = ? AND " + column + " = 1")) {
            ps.setString(1, filename);
            ps.setString(2, user);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    public static List<AclEntry> getPermissions(String filename) throws SQLException {
        List<AclEntry> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM file_acl WHERE filename = ?")) {
            ps.setString(1, filename);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AclEntry entry = new AclEntry();
                entry.setFilename(rs.getString("filename"));
                entry.setOwner(rs.getString("owner"));
                entry.setSharedUser(rs.getString("shared_user"));
                entry.setCanRead(rs.getInt("can_read") == 1);
                entry.setCanWrite(rs.getInt("can_write") == 1);
                list.add(entry);
            }
        }
        return list;
    }
    public static void revokeAccess(String filename, String user) throws SQLException {
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(
             "DELETE FROM file_acl WHERE filename = ? AND shared_user = ?")) {
        ps.setString(1, filename);
        ps.setString(2, user);
        ps.executeUpdate();
    }
}

}
