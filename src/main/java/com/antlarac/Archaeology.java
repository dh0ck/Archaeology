package com.antlarac;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;

import com.antlarac.UiElements.Ui;
import com.antlarac.UiElements.UiRenameFlow;

import java.net.URISyntaxException;
import java.sql.SQLException;
import javax.swing.JPanel;

public class Archaeology implements BurpExtension {

  private MontoyaApi api;

  @Override
  public void initialize(MontoyaApi api){
//    Logic logic = new Logic();
    try {
      Database database = new Database();
    //        Database database = new Database(api);



//      List<ProxyHttpRequestResponse> history = logic.getFullHistory(api);
//    System.out.println(history);
    Logging logging = api.logging();
    Logic logic = new Logic(logging, database);
    Ui ui = null;
    try {
      ui = new Ui(api, logging, database, logic, new UiRenameFlow());
//      ui = new Ui(api, logging, new Database(), new UiRenameFlow());
      System.out.println("ui");
    } catch (SQLException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    JPanel mainPanel = ui.createUi();
    api.userInterface().registerSuiteTab("Archaeology", mainPanel);

    logging.logToOutput("Archaeology successfully loaded.");
    } catch (SQLException | ClassNotFoundException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

}
