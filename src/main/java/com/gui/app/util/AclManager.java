package com.gui.app.util;


import com.gui.app.model.AclEntry;
import java.sql.*;
import java.util.*;
import com.gui.app.session.*;

public class AclManager {

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:comp20081.db");
    }

    static {
        try (Connection conn = getConnection()) {
           String sql = "CREATE TABLE IF NOT EXISTS file_acl (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " filename TEXT UNIQUE NOT NULL," +
                " owner TEXT UNIQUE NOT NULL," +
                " shared_user TEXT UNIQUE NOT NULL," +
                " can_read INTEGER DEFAULT 0," +
                " can_write INTEGER DEFAULT 0 " + 
                ")";
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
           System.out.println("file_acl created!! "+sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void grantAccess(AclEntry entry) throws SQLException {
    String checkSql = "SELECT COUNT(*) FROM file_acl WHERE filename=? AND owner=? AND shared_user=?";
    String insertSql = "INSERT INTO file_acl (filename, owner, shared_user, can_read, can_write) VALUES (?, ?, ?, ?, ?)";
    String updateSql = "UPDATE file_acl SET can_read=?, can_write=? WHERE filename=? AND owner=? AND shared_user=?";

    try (Connection conn = getConnection();
         PreparedStatement checkPs = conn.prepareStatement(checkSql)) {

        checkPs.setString(1, entry.getFilename());
        checkPs.setString(2, entry.getOwner());
        checkPs.setString(3, entry.getSharedUser());

        try (ResultSet rs = checkPs.executeQuery()) {
            rs.next();
            if (rs.getInt(1) == 0) {
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    insertPs.setString(1, entry.getFilename());
                    insertPs.setString(2, entry.getOwner());
                    insertPs.setString(3, entry.getSharedUser());
                    insertPs.setInt(4, entry.isCanRead() ? 1 : 0);
                    insertPs.setInt(5, entry.isCanWrite() ? 1 : 0);
                    insertPs.executeUpdate();
                }
            } else {
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    updatePs.setInt(1, entry.isCanRead() ? 1 : 0);
                    updatePs.setInt(2, entry.isCanWrite() ? 1 : 0);
                    updatePs.setString(3, entry.getFilename());
                    updatePs.setString(4, entry.getOwner());
                    updatePs.setString(5, entry.getSharedUser());
                    updatePs.executeUpdate();
                }
            }
        }
    }
}

public static boolean isOwner(String username, String filename) {
    String sql = "SELECT owner FROM file_acl WHERE filename = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, filename);
        ResultSet rs = stmt.executeQuery();
        return rs.next() && username.equals(rs.getString("owner"));
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
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
