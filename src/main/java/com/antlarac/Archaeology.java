package com.antlarac;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;

import javax.swing.JPanel;

public class Archaeology implements BurpExtension {

  Ui ui = new Ui();
  @Override
  public void initialize(MontoyaApi api){


    Logging logging = api.logging();

    JPanel mainPanel = ui.createUi();
    api.userInterface().registerSuiteTab("Archaeology", mainPanel);
    logging.logToOutput("Archaeology successfully loaded.");
  }

}
