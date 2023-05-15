package Servis;


import java.io.IOException;
import java.sql.*;
import java.util.Arrays;

import static Servis.FileUtils.readTxt;

public class DatabaseConnection {
    private static Connection conn = null;
    private static String[] connectionInfoTemp;
    private static final String[] conn_type;

    static {
        try {
            conn_type = readTxt("dat/connection_type.txt", 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static Connection getConnection() {
        try {
            switch (conn_type[0]) {
                case "sqlite" -> conn = getSQLiteConnection();
                case "mysql" -> conn = getMySQLConnection();
                default -> throw new IllegalArgumentException("Nepoznati tip baze podataka: " + Arrays.toString(conn_type));
            }
            return conn;
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static Connection getMySQLConnection() throws IOException {
        connectionInfoTemp = readTxt("dat/mysql_connection.txt", 3);

        String URL = connectionInfoTemp[0];
        String USERNAME = connectionInfoTemp[1].replace("*", "");
        String PASSWORD = connectionInfoTemp[2].replace("*", "");

        Connection conn = null;
        try {
            closeConnection();
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }
    public static Connection getSQLiteConnection() throws IOException {
        connectionInfoTemp = readTxt("dat/sqlite_connection.txt", 2);

        try {
            closeConnection();
            if (conn == null || conn.isClosed()) {
                Class.forName(connectionInfoTemp[0]);
                conn = DriverManager.getConnection(connectionInfoTemp[1]);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}