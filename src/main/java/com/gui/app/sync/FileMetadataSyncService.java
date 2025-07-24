package com.gui.app.sync;
import java.sql.*;
import java.util.*;
import com.gui.app.remote.RemoteDatabaseManager;
import com.gui.app.db.DB;
public class FileMetadataSyncService {
    public static void syncFilesToMySQL() {
        String sql = "SELECT * FROM temp_files WHERE synced = 0";

        try (
            Connection localConn = DB.getConnection();
            Connection remoteConn = RemoteDatabaseManager.getConnection();
            Statement localStmt = localConn.createStatement();
            ResultSet rs = localStmt.executeQuery(sql);
        ) {
            while (rs.next()) {
                String filename = rs.getString("filename");
                String path = rs.getString("path");
                String status = rs.getString("status");

                PreparedStatement insert = remoteConn.prepareStatement("""
                    REPLACE INTO file_metadata (filename, path, status, owner)
                    VALUES (?, ?, ?, ?)
                """);

                insert.setString(1, filename);
                insert.setString(2, path);
                insert.setString(3, status);
                insert.setString(4, "owner_from_session"); // Replace or pass dynamically
                insert.executeUpdate();

                // mark as synced
                PreparedStatement markSynced = localConn.prepareStatement("UPDATE temp_files SET synced = 1 WHERE filename = ?");
                markSynced.setString(1, filename);
                markSynced.executeUpdate();
            }

            System.out.println("[Sync] File metadata synced to MySQL.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
public static void syncMySQLToSQLite() {
    try (
        Connection localConn = DB.getConnection();
        Connection remoteConn = RemoteDatabaseManager.getConnection();
    ) {
        Statement remoteStmt = remoteConn.createStatement();
        ResultSet rs = remoteStmt.executeQuery("SELECT * FROM file_metadata");

        while (rs.next()) {
            String filename = rs.getString("filename");
            String path = rs.getString("path");
            String status = rs.getString("status");
            String owner = rs.getString("owner");
            Timestamp updatedAt = rs.getTimestamp("updated_at");

            // Check if file exists locally
            PreparedStatement localCheck = localConn.prepareStatement("SELECT updated_at FROM temp_files WHERE filename = ?");
            localCheck.setString(1, filename);
            ResultSet localRs = localCheck.executeQuery();

            if (!localRs.next()) {
                // New file — insert
                PreparedStatement insert = localConn.prepareStatement("""
                    INSERT INTO temp_files (filename, path, status, synced, remote_updated_at)
                    VALUES (?, ?, ?, 1, ?)
                """);
                insert.setString(1, filename);
                insert.setString(2, path);
                insert.setString(3, status);
                insert.setTimestamp(4, updatedAt);
                insert.executeUpdate();

            } else {
                // Existing file — check conflict
                Timestamp localTime = localRs.getTimestamp("updated_at");
                if (localTime == null || updatedAt.after(localTime)) {
                    // MySQL is newer — update local
                    PreparedStatement update = localConn.prepareStatement("""
                        UPDATE temp_files SET path = ?, status = ?, synced = 1, remote_updated_at = ?
                        WHERE filename = ?
                    """);
                    update.setString(1, path);
                    update.setString(2, status);
                    update.setTimestamp(3, updatedAt);
                    update.setString(4, filename);
                    update.executeUpdate();
                }
            }
        }

        System.out.println("[Sync] MySQL ➜ SQLite complete.");

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

}
