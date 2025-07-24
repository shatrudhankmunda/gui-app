package com.gui.app.db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    private static final String LOCAL_DB = "jdbc:sqlite:comp20081.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(LOCAL_DB);
    }
}
