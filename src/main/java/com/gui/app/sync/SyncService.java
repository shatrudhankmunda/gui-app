package com.gui.app.sync;
public class SyncService {
    public static void syncAll() {
        // push local ? remote
        FileMetadataSyncService.syncFilesToMySQL();
        SessionSyncService.syncSessionsToMySQL();

        // pull remote ? local
        FileMetadataSyncService.syncMySQLToSQLite();
    }
}
