package com.antlarac;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;
import burp.api.montoya.logging.Logging;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.antlarac.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logic {

    private static final Logger log = LoggerFactory.getLogger(Logic.class);
    private final Logging logging;
    private final Database database;

    public Logic(Logging logging, Database database) {
        this.logging = logging;
        this.database = database;
    }

    public List<ProxyHttpRequestResponse> getFullHistory(MontoyaApi api) {
        return api.proxy().history();
    }

    public void populateRequestAndResponseBoxWhenClickingAHistoryTableRow() {
        logging.logToOutput("populating request and response");
    }

    public List<ProxyHttpRequestResponse> filterHistoryList(List<ProxyHttpRequestResponse> requestsResponses, String filter) {
        List<ProxyHttpRequestResponse> filteredHistoryList = new ArrayList<>();
        for (ProxyHttpRequestResponse item : requestsResponses) {
            boolean matches = false;
            // TODO: check if filter is in (if checkbox is ticked)
            //  - any of the columns
            //  - the request
            //  - the response

            if (matches) {
               filteredHistoryList.add(item);
            }
        }
        return filteredHistoryList;
    }

    public void saveCurrentFlow() {

        logging.logToOutput("1111");
        Connection conn = null;
        try {
            logging.logToOutput("2222");
            conn = this.database.connectToDatabase();

            try {
                logging.logToOutput("333");
                database.saveTable(conn, database.timestamp(new Timestamp(System.currentTimeMillis())), false);
                logging.logToOutput("444");
            } catch (SQLException ex) {
                logging.logToOutput("555");
                throw new RuntimeException(ex);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // TODO: save a single tab to a table in the DB.
            //  check if the table already exists and offer to append or to clear it

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void saveAllFlows() {
        // TODO: save all tabs to different tables in the DB

    }

    public void loadSpecificFlow() {
        logging.logToOutput("loading specific flow");
        // TODO: introspect DB and show existing tables, allowing to select one and create a new tab with it
    }

    public void loadAllFlows() {
        // TODO: load all the tabs stored in a DB, one per table
    }
}
