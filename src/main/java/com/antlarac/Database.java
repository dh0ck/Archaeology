package com.antlarac;

import burp.api.montoya.logging.Logging;
import com.antlarac.UiElements.SharedElements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.sql.*;
import java.text.SimpleDateFormat;

import static com.antlarac.UiElements.SharedElements.tabbedPane;

public class Database {

    private static final Logger log = LoggerFactory.getLogger(Database.class);
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

    public void saveTable(Connection conn, String ts, boolean all) throws SQLException {
        logging.logToOutput(currentDirectory);
        logging.logToOutput("heyyxxx");
        if (all) {
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                Component component = SharedElements.tabbedPane.getComponentAt(i);
                // TODO: these castings seem to not be working
//                Component component0 = component.
                JPanel panel = (JPanel) component;
                Component[] components = panel.getComponents();
                JSplitPane splitPaneTop = (JSplitPane) components[0];
                JScrollPane scrollPane = (JScrollPane) splitPaneTop.getLeftComponent();
                JTable historyTable = (JTable) scrollPane.getViewport().getView();

                for (int j = 0; j < historyTable.getColumnCount(); j++) {
                    TableColumn column = historyTable.getColumnModel().getColumn(j);
                    logging.logToOutput("column " + j + ": " + column.getHeaderValue());
                }
            }
        } else {
            // TODO: aqui iria para cuando se guarda solo los datos de la tab seleccionada actualmente
            //  pero no esta guardando nada en el log
            Component component = tabbedPane.getSelectedComponent();
            logging.logToOutput(component.toString());
            JPanel panel = (JPanel) component;
            Component[] components = panel.getComponents();
            logging.logToOutput("hhhh");
            logging.logToOutput(components.toString());

        }
//        insertHistoryLine();
        insertHistoryLine(conn, "http://darkreader.xxx", "GET", "/blog/posts.json", "?test=1", true, 200, 4309, "JSON", "json", "title1", "notes here", true, "123.234.23.123", "session: 123", ts, 8080);
        logging.logToError("asdfasdf");
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
        pstmt.executeUpdate();

        stmt.close();
    }

//    private String currentDirectory = getClass().getResource(".").getPath();
    private String currentDirectory = System.getProperty("user.dir");
    public String url = "jdbc:sqlite:" + currentDirectory + "/archaeology.db";

    public Connection connectToDatabase() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public String timestamp(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ts = sdf.format(timestamp);
        return ts;
    }

    public Database() throws SQLException, ClassNotFoundException, URISyntaxException {
        Class.forName("org.sqlite.JDBC");
        Connection conn = connectToDatabase();
        DatabaseMetaData metadata = conn.getMetaData();
        ResultSet tables = metadata.getTables(null, null, "history", null);
        // TODO: Something wrong here, jumps to the else block when there is no history table
        if (!tables.next()) {
            logging.logToOutput("Creating history table...");
            try {
                this.createHistoryTable(conn, url);
                logging.logToOutput("History table created correctly.");

            } catch (SQLException e) {
                logging.logToError("Couldn't create history table created.");
            }
        } else {
            logging.logToOutput("History table already exists.");
        }

        try {
            String ts = timestamp(new Timestamp(System.currentTimeMillis()));
            logging.logToOutput("Trying to instert history line");
            this.insertHistoryLine(conn, "http://darkreader.org", "GET", "/blog/posts.json", "?test=1", true, 200, 4309, "JSON", "json", "title1", "notes here", true, "123.234.23.123", "session: 123", ts, 8080);
//            this.saveTable(conn, ts, false);
        } catch (SQLException e) {
            logging.logToError("Couldn't insert history line");
        }
        conn.close();
    }

}
