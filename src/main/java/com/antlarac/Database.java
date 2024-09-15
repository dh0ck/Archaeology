package com.antlarac;

import burp.api.montoya.logging.Logging;

import java.io.PrintStream;
import java.sql.*;
import java.text.SimpleDateFormat;

public class Database {

    private void createHistoryTable(Connection conn, String url) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "host VARCHAR(300)," +
                "method VARCHAR(10)," +
                "url TEXT," +
                "params TEXT," +
                "edited BOOLEAN," +
                "statusCode INTEGER," +
                "length INTEGER," +
                "mimeType VARCHAR(100)," +
                "extension VARCHAR(10)," +
                "title VARCHAR(256)," +
                "notes TEXT," +
                "tls BOOLEAN," +
                "ip VARCHAR(256)," +
                "cookies TEXT," +
                "dateTime VARCHAR(20)," +
                "port INTEGER" +
                ");"
        );
        stmt.close();
    }

    private void insertHistoryLine(Connection conn, String host, String method, String url, String params, Boolean edited, Integer statusCode, Integer length, String mimeType, String extension, String title, String notes, Boolean tls, String ip, String cookies, String dateTime, Integer port) throws SQLException {

        Statement stmt = conn.createStatement();

        String insertSQL = "INSERT INTO history(host, method, url, params, edited, statusCode, length, mimeType, extension, title, notes, tls, ip, cookies, dateTime, port) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

        PreparedStatement pstmt = conn.prepareStatement(insertSQL);
        pstmt.setString(1, host);
        pstmt.setString(2, method);
        pstmt.setString(3, url);
        pstmt.setString(4, params);
        pstmt.setBoolean(5, edited);
        pstmt.setInt(6, statusCode);
        pstmt.setInt(7, length);
        pstmt.setString(8, mimeType);
        pstmt.setString(9, extension);
        pstmt.setString(10, title);
        pstmt.setString(11, notes);
        pstmt.setBoolean(12, tls);
        pstmt.setString(13, ip);
        pstmt.setString(14, cookies);
        pstmt.setString(15, dateTime);
        pstmt.setInt(16, port);
//        );
        pstmt.executeUpdate();

        stmt.close();
    }

//    public Database(MontoyaApi api) throws SQLException, ClassNotFoundException {
    public Database() throws SQLException, ClassNotFoundException {
        // uncomment next line for running in IDE, commont for loading in Burp
        Class.forName("org.sqlite.JDBC");
        System.out.println("db");
        String url = "jdbc:sqlite:archaeology.db";

        Logging logging = new Logging() {
            @Override
            public PrintStream output() {
                return null;
            }

            @Override
            public PrintStream error() {
                return null;
            }

            @Override
            public void logToOutput(String message) {

            }

            @Override
            public void logToError(String message) {

            }

            @Override
            public void logToError(String message, Throwable cause) {

            }

            @Override
            public void logToError(Throwable cause) {

            }

            @Override
            public void raiseDebugEvent(String message) {

            }

            @Override
            public void raiseInfoEvent(String message) {

            }

            @Override
            public void raiseErrorEvent(String message) {

            }

            @Override
            public void raiseCriticalEvent(String message) {

            }
        };


        Connection conn = DriverManager.getConnection(url);
        DatabaseMetaData metadata = conn.getMetaData();
        ResultSet tables = metadata.getTables(null, null, "history", null);
        if (!tables.next()) {
//            System.out.println("Creating history table...");
            logging.logToOutput("Creating history table...");
            try {
                this.createHistoryTable(conn, url);
                logging.logToOutput("History table created correctly.");
//                System.out.println("History table created correctly.");

            } catch (SQLException e) {
                logging.logToError("Couldn't create history table created.");
//                System.out.println("Couldn't create history table created.");
//                log.error("Couldn't create history table.");
            }
        } else {
//            log.info("History table already exists.");
            logging.logToOutput("History table already exists.");
//            System.out.println("History table already exists.");
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String ts = sdf.format(timestamp);

            this.insertHistoryLine(conn, "http://darkreader.org", "GET", "/blog/posts.json", "?test=1", true, 200, 4309, "JSON", "json", "title1", "notes here", true, "123.234.23.123", "session: 123", ts, 8080);
        } catch (SQLException e) {
            logging.logToOutput("Couldn't insert history line");
//            System.out.println("Couldn't insert history line");
        }
        conn.close();
    }

}
