package com.antlarac;

import burp.api.montoya.logging.Logging;
import com.antlarac.UiElements.SharedElements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import javax.swing.*;
import javax.swing.table.TableColumn;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.table.TableModel;
import java.awt.*;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;

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
                "dateTime VARCHAR(40)," +
                "port INTEGER" +
                ");"
        );
        stmt.close();
    }

    public void saveTable(Connection conn, String ts, boolean full) throws SQLException, IOException {
        logging.logToOutput(currentDirectory);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX[v]");
        Component component = tabbedPane.getSelectedComponent();
        JSplitPane pane1 = (JSplitPane) component;
        var pane2 = pane1.getTopComponent();
        var pane3 = (JSplitPane) pane2;
        var pane4 = pane3.getLeftComponent();
        JPanel pane5 = (JPanel) pane4;
        var pane6 = pane5.getComponents(); // este es para el filtro

        var pane7 = pane3.getRightComponent();
        var pane8 = (JScrollPane) pane7;
        var pane9 = pane8.getComponents();
        var pane10 = (JViewport) pane9[0];
        var pane11 = pane10.getView();
        JTable table = (JTable) pane11;
        if (full) {
            table.selectAll();
            logging.logToOutput("all selected");
        }
        // TODO: aqui iria para cuando se guarda solo los datos de la tab seleccionada actualmente
        //  pero no esta guardando nada en el log

        int[] selectedRows = table.getSelectedRows();

        for (int rowIndex : selectedRows) {
            TableModel model = table.getModel();

//                int index = (int) model.getValueAt(rowIndex, 0);
            String host = (String) model.getValueAt(rowIndex, 1);
            String path = (String) model.getValueAt(rowIndex, 2);
            int port = (int) model.getValueAt(rowIndex, 3);
            String method = (String) model.getValueAt(rowIndex, 4);
            boolean edited = (boolean) model.getValueAt(rowIndex, 5);
            ZonedDateTime dateTime = (ZonedDateTime) model.getValueAt(rowIndex, 6);
            String date = dateTime.format(formatter);
            // TODO: aclarar que poner en params en lugar de testparams que hay ahora, y en el resto de campos raros
            insertHistoryLine(conn, host, method, path, "testparams", edited, 200, 4309, "JSON", "json", "title1", "notes here", true, "123.234.23.123", "session: 123", date, port);
            }

        if (full) {
            table.clearSelection();
            // TODO: arreglar esto, hay que seleccionar las que el user selecciono a mano, porque en la fila anterior
            //  se estan limpiando al haber dado al boton de save full table, que funciona seleccionando todas a saco y guardandolas:

//                for (int index0 = 0; index0 < selectedRows.length; index0++) {
//                    int index1 = selectedRows[index0];
//                    table.getSelectionModel().addSelectionInterval(index0, index1);
//                }
        }
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
            this.insertHistoryLine(conn, "http://darkreader.org", "GET", "/blog/posts.json", "?test=1", true, 200, 4309, "JSON", "json", "title", "notes here", true, "123.234.23.123", "session: 123", ts, 8080);
//            this.saveTable(conn, ts, false);
        } catch (SQLException e) {
            logging.logToError("Couldn't insert history line");
        }
        conn.close();
    }

}
