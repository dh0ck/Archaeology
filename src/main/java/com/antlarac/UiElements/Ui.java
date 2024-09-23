package com.antlarac.UiElements;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;
import com.antlarac.Database;
import com.antlarac.Logic;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Ui {
  private final MontoyaApi api;
  JTabbedPane tabbedPane;
  private final Logging logging;
  JComboBox<String> flowSelector = new JComboBox<>();
  Logic logic;
  private String activeFlow;
  private Database database;
  private final UiRenameFlow uiRenameFlow;

  public Ui(MontoyaApi api, Logging logging, Database database, UiRenameFlow uiRenameFlow) throws ClassNotFoundException, SQLException {
    this.api = api;
    this.logging = logging;
    this.logic = new Logic(this.logging);
    this.database = database;
    this.uiRenameFlow = uiRenameFlow;
  }

  private DefaultTableModel createTableModel() {
    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.addColumn("#");
    tableModel.addColumn("Host");
    tableModel.addColumn("Path");
    tableModel.addColumn("Port");
    tableModel.addColumn("Method");
//    tableModel.addColumn("Status");
    tableModel.addColumn("Edited");
    tableModel.addColumn("Time");

    return tableModel;
  }

  private List<String> generateTextForRequestOrResponse(ProxyHttpRequestResponse requestResponse) {
    StringBuilder textRequest = new StringBuilder();
    List<HttpHeader> requestHeaders = requestResponse.request().headers();
    String method = requestResponse.request().method();
    String endpoint = requestResponse.request().path();
    String HTTPVersion = requestResponse.request().httpVersion();
    textRequest.append(method).append(" ").append(endpoint).append(" ").append(HTTPVersion).append("\n");
    for (HttpHeader header : requestHeaders) {
      textRequest.append(header.name()).append(": ").append(header.value()).append("\n");
    }
    textRequest.append("\n").append(requestResponse.request().body().toString());

    StringBuilder textResponse = new StringBuilder();
    List<HttpHeader> responseHeaders = requestResponse.response().headers();
    for (HttpHeader header : responseHeaders) {
      textResponse.append(header.name()).append(": ").append(header.value()).append("\n");
    }
    textResponse.append("\n").append(requestResponse.response().body().toString());

    return List.of(textRequest.toString(), textResponse.toString());
  }

  private JTable createHistoryTable(List<ProxyHttpRequestResponse> historyList, JSplitPane horizontalSplitPane) {
    DefaultTableModel tableModel = createTableModel();
    logging.logToOutput("Creating history table...");
    int number = 0;
    for (ProxyHttpRequestResponse requestResponseObject : historyList) {
      number++;
      String host = requestResponseObject.host();
      String url = requestResponseObject.path();
      int port = requestResponseObject.port();
      String method = requestResponseObject.method();

      boolean edited = requestResponseObject.edited();
      ZonedDateTime time = requestResponseObject.time();

      tableModel.addRow(new Object[]{number, host, url, port, method, edited, time});
    }
    JTable historyTable = new JTable(tableModel);
    historyTable.setMinimumSize(new Dimension(100, 100));

    historyTable.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        int row = historyTable.rowAtPoint(e.getPoint());
        JScrollPane leftScrollPane = (JScrollPane) horizontalSplitPane.getLeftComponent();
        JTextArea leftTextArea = (JTextArea) leftScrollPane.getViewport().getView();
        String requestText = generateTextForRequestOrResponse(historyList.get(row)).get(0);
        leftTextArea.setText(requestText);

        JScrollPane rightScrollPane = (JScrollPane) horizontalSplitPane.getRightComponent();
        JTextArea rightTextArea = (JTextArea) rightScrollPane.getViewport().getView();
        String responseText = generateTextForRequestOrResponse(historyList.get(row)).get(1);
        rightTextArea.setText(responseText);
      }
    });

    return historyTable;
  }

  private JTextArea createTextArea() {
      JTextArea textArea = new JTextArea();
      textArea.setLineWrap(true);
      textArea.setWrapStyleWord(true);
      return textArea;
  }

  private JSplitPane createHorizontalSplitPane() {

    JSplitPane horizontalSplitPane = new JSplitPane(
        JSplitPane.HORIZONTAL_SPLIT,
        new JScrollPane(createTextArea()),
        new JScrollPane(createTextArea())
    );
    horizontalSplitPane.setContinuousLayout(true);
    horizontalSplitPane.setDividerLocation(1000);
    horizontalSplitPane.setDividerSize(1);

    return horizontalSplitPane;
  }

  private JSplitPane createVerticalSplitPane(List<ProxyHttpRequestResponse> historyList) {

    JSplitPane horizontalSplitPane = createHorizontalSplitPane();
    JTable historyTable = createHistoryTable(historyList, horizontalSplitPane);

    JSplitPane verticalSplitPane = new JSplitPane(
        JSplitPane.VERTICAL_SPLIT,
        new JScrollPane(historyTable),
        new JScrollPane(horizontalSplitPane)
    );

    verticalSplitPane.setContinuousLayout(true);
    verticalSplitPane.setDividerLocation(500);
    verticalSplitPane.setDividerSize(1);

    return verticalSplitPane;
  }

  private final JFileChooser chooser = new JFileChooser();

  private void updateFlowSelectorComboBox() {
    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
    if (Objects.isNull(tabbedPane)) {
      model.addElement("");
    } else {
      for (int i = 0; i < tabbedPane.getTabCount(); i++) {
        model.addElement(tabbedPane.getTitleAt(i));
      }
    }
    this.flowSelector.setModel(model);
  }

  private JPanel createButtonsPanel() {
    JButton buttonAddNewTab = new JButton("Add New Tab");
    buttonAddNewTab.addActionListener(e -> addTab());

    JButton buttonCreateFullHistoryTab = new JButton("Create Full History");
    buttonCreateFullHistoryTab.addActionListener(e -> addFullHistoryToNewTab());

    JButton buttonDeleteTab = new JButton("Delete Tab");
    buttonDeleteTab.addActionListener(e -> removeCurrentlyActiveTab());

    JButton saveButton = new JButton("Save");
    //TODO poner esto en una funcion externa, como hago en el boton anterior, y todas estas acciones de botones ponerla sen un archivo externo
    saveButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        logging.logToOutput("123");
          Connection conn = null;
          try {
              conn = database.connectToDatabase();
          } catch (SQLException ex) {
              throw new RuntimeException(ex);
          }

          try {
              database.saveTable(conn, database.timestamp(new Timestamp(System.currentTimeMillis())));
          } catch (SQLException ex) {
              throw new RuntimeException(ex);
          }
          logging.logToOutput("345");
      }
    });

    JButton loadButton = new JButton("Load");

    updateFlowSelectorComboBox();
    JButton moveSelectedToFlow = new JButton("Move Selected to Flow");

    JButton setSelectedToActiveFlow = new JButton("Set selected Flow as active");
    setSelectedToActiveFlow.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        activeFlow = Objects.requireNonNull(flowSelector.getSelectedItem()).toString();
        logging.logToOutput(activeFlow);
      }
    });

    JTextField dbPath = new JTextField();

    JButton dbPathButton = new JButton("DB Path");
    dbPathButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int chosenPath = chooser.showOpenDialog(null);
        if (chosenPath == JFileChooser.APPROVE_OPTION) {
          dbPath.setText(chooser.getSelectedFile().getAbsolutePath());
          // TODO: actually apply this path to the DB so that it gets stored there,
          // and save that path also to some config file for loading it the next time the extension loads

        }
        logging.logToOutput(String.valueOf(chosenPath));
      }
    });

    JButton renameFlow = new JButton("Rename Flow");
    renameFlow.addActionListener(e -> renameCurrentFlow());


    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    buttonsPanel.add(buttonAddNewTab);
    buttonsPanel.add(buttonDeleteTab);
    buttonsPanel.add(buttonCreateFullHistoryTab);
    buttonsPanel.add(loadButton);
    buttonsPanel.add(saveButton);
    buttonsPanel.add(moveSelectedToFlow);
    buttonsPanel.add(setSelectedToActiveFlow);
    buttonsPanel.add(this.flowSelector);
    buttonsPanel.add(new JLabel("| Database: "));
    buttonsPanel.add(dbPath);
    buttonsPanel.add(dbPathButton);
    buttonsPanel.add(renameFlow);
    return buttonsPanel;
  }

  private void renameCurrentFlow() {
    this.uiRenameFlow.setVisible();
  }

  private JTabbedPane createTabbedPane() {
    tabbedPane = SharedTabbedPane.tabbedPane;
    tabbedPane.addTab("0 - Flow 1", createVerticalSplitPane(List.of()));
    updateFlowSelectorComboBox();
    return tabbedPane;
  }

  private void addTab() {
    int numTabs = tabbedPane.getTabCount();
    tabbedPane.addTab(String.format("%d - New Flow",numTabs), createVerticalSplitPane(List.of()));
    tabbedPane.setSelectedIndex(numTabs);
    updateFlowSelectorComboBox();
  }

  private void removeCurrentlyActiveTab() {
    int selectedIndex = tabbedPane.getSelectedIndex();

    if (selectedIndex != -1) {
      tabbedPane.removeTabAt(selectedIndex);
    }
    updateFlowSelectorComboBox();
  }

  private void addFullHistoryToNewTab(){
    int numTabs = tabbedPane.getTabCount();
    tabbedPane.addTab(String.format("%d - Full History",numTabs), createVerticalSplitPane(logic.getFullHistory(this.api)));
    tabbedPane.setSelectedIndex(numTabs);
    updateFlowSelectorComboBox();
  }

  private JPanel createMainPanel() {
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(createButtonsPanel(), BorderLayout.NORTH);
    mainPanel.add(createTabbedPane(), BorderLayout.CENTER);
    return mainPanel;
  }

  public JPanel createUi() {
    return createMainPanel();
  }
}
