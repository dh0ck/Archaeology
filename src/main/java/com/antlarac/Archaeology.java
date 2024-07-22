package com.antlarac;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;

import burp.api.montoya.proxy.ProxyHttpRequestResponse;
import java.util.List;
import javax.swing.JPanel;

public class Archaeology implements BurpExtension {

  private MontoyaApi api;

  @Override
  public void initialize(MontoyaApi api){
    Logic logic = new Logic();
    List<ProxyHttpRequestResponse> history = logic.getFullHistory(api);
    Logging logging = api.logging();
    Ui ui = new Ui(api, logging);
    JPanel mainPanel = ui.createUi();
    api.userInterface().registerSuiteTab("Archaeology", mainPanel);

    logging.logToOutput(String.valueOf(history.size()));
    logging.logToOutput(history.get(7).request().toString());
    logging.logToOutput(history.get(7).response().toString());
    logging.logToOutput("Archaeology successfully loaded.");
  }

}
