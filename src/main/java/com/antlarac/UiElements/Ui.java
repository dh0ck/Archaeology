package com.antlarac.UiElements;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;
import com.antlarac.Database;
import com.antlarac.Logic;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import static com.antlarac.UiElements.SharedElements.flowSelector;
import static com.antlarac.UiElements.SharedElements.updateFlowSelectorComboBox;

public class Ui {
  private final MontoyaApi api;
  JTabbedPane tabbedPane;
  private final Logging logging;
  Logic logic;
  private String activeFlow;
  private Database database = new Database();
  private final UiRenameFlow uiRenameFlow;
  private final JFileChooser chooser = new JFileChooser();
  private String highlightColor = "#629fea";

  public Ui(MontoyaApi api, Logging logging, Database database, Logic logic, UiRenameFlow uiRenameFlow) throws ClassNotFoundException, SQLException, URISyntaxException {
    this.api = api;
    this.logging = logging;
    this.logic =  logic;
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

  private static String getStatusText(int statusCode) {
    switch (statusCode) {
      case 200: return "OK";
      case 201: return "Created";
      case 204: return "No Content";
      case 301: return "Moded Permanently";
      case 304: return "Not Modified";
      case 400: return "Bad Request";
      case 401: return "Unauthorized";
      case 403: return "Forbidden";
      case 404: return "Not Found";
      case 500: return "Internal Server Error";
      // Add more status codes as needed
      default: return "Unknown Status Code";
    }
  }

  private List<String> generateTextForRequestOrResponse(ProxyHttpRequestResponse requestResponse) {
    StringBuilder textRequest = new StringBuilder();
    List<HttpHeader> requestHeaders = requestResponse.request().headers();
    String method = requestResponse.request().method();
    String endpoint = requestResponse.request().path();
    String HTTPVersion = requestResponse.request().httpVersion();
    textRequest.append(String.format("<b><font color=%s>", this.highlightColor)).append(method).append(" ").append(endpoint).append(" ").append(HTTPVersion).append("</font></b><br>");
    for (HttpHeader header : requestHeaders) {
      textRequest.append(String.format("<b><font color=%s>", this.highlightColor)).append(header.name()).append(":</font></b> ").append(header.value()).append("<br>");
    }
    textRequest.append("\n").append(requestResponse.request().body().toString());

    StringBuilder textResponse = new StringBuilder();
    List<HttpHeader> responseHeaders = requestResponse.response().headers();
    String responseCode = String.valueOf(requestResponse.response().statusCode());
    textResponse.append(String.format("<b><font color=%s>", this.highlightColor)).append(responseCode).append(" ").append(getStatusText(requestResponse.response().statusCode())).append("</font></b><br>");
    for (HttpHeader header : responseHeaders) {
      textResponse.append(String.format("<b><font color=%s>", this.highlightColor)).append(header.name()).append(":</font></b> ").append(header.value()).append("<br>");
    }
    textResponse.append("\n").append(requestResponse.response().body().toString());
    // TODO: the text is not wrapping in the JEditorPanes

    // TODO: HTML responses are being rendered automatically in the response pane. allow for the possibility to render or not

    return List.of(textRequest.toString(), textResponse.toString());
  }

  private void filterHistoryItems() {
    // TODO: save current table to temporary table
    //  apply the filter function
    //  replace the filtered table returned by the filter function (in logic)
    logging.logToOutput("applying filter");
  }

  private void filterFileTypes() {
    // TODO: create mini window to select response codes and filetypes
  }
  private JPanel filterPanel() {
    JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    JTextField filterTextField = new JTextField(25);
    JButton filterButton = new JButton("Apply");
    filterButton.addActionListener(e -> this.filterHistoryItems());
    JCheckBox checkBoxColumns = new JCheckBox();
    JCheckBox checkBoxRequest = new JCheckBox();
    JCheckBox checkBoxResponse = new JCheckBox();
    JButton buttonAdvancedFilters = new JButton("Advanced filters");
    buttonAdvancedFilters.addActionListener(e -> filterFileTypes());

    filterPanel.add(new JLabel("Filter: "));
    filterPanel.add(filterTextField);
    filterPanel.add(filterButton);
    filterPanel.add(new JLabel("Apply filter to:    "));
    filterPanel.add(checkBoxColumns);
    filterPanel.add(new JLabel("Columns"));
    filterPanel.add(checkBoxRequest);
    filterPanel.add(new JLabel("Requests"));
    filterPanel.add(checkBoxResponse);
    filterPanel.add(new JLabel("Responses"));
    filterPanel.add(buttonAdvancedFilters);
    return filterPanel;
  }
  private JSplitPane createHistoryTable(List<ProxyHttpRequestResponse> historyList, JSplitPane horizontalSplitPane) {
    DefaultTableModel tableModel = createTableModel();
    int number = 0;
    for (ProxyHttpRequestResponse requestResponseObject : historyList) {
      number++;
      //TODO: find alternative to the following deprecated methods
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
        JEditorPane leftTextArea = (JEditorPane) leftScrollPane.getViewport().getView();
        String requestText = generateTextForRequestOrResponse(historyList.get(row)).get(0);
        leftTextArea.setText(requestText);

        JScrollPane rightScrollPane = (JScrollPane) horizontalSplitPane.getRightComponent();
        JEditorPane rightTextArea = (JEditorPane) rightScrollPane.getViewport().getView();
        String responseText = generateTextForRequestOrResponse(historyList.get(row)).get(1);
        rightTextArea.setText(responseText);
      }
    });
    JPanel filterPanel = this.filterPanel();
    JSplitPane filterAndTableSplitPane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            filterPanel,
            new JScrollPane(historyTable)
    );

    return filterAndTableSplitPane;
  }

  private JEditorPane createEditorPane() {
    JEditorPane editorPane = new JEditorPane("text/html", "");
    return editorPane;
  }

  private JSplitPane createHorizontalSplitPane() {

    JSplitPane horizontalSplitPane = new JSplitPane(
        JSplitPane.HORIZONTAL_SPLIT,
        new JScrollPane(createEditorPane()),
        new JScrollPane(createEditorPane())
    );
    horizontalSplitPane.setContinuousLayout(true);
    horizontalSplitPane.setDividerLocation(1000);
    horizontalSplitPane.setDividerSize(1);

    return horizontalSplitPane;
  }

  private JSplitPane createVerticalSplitPane(List<ProxyHttpRequestResponse> historyList) {

    JSplitPane horizontalSplitPane = createHorizontalSplitPane();
    JSplitPane historyTable = createHistoryTable(historyList, horizontalSplitPane);

    JSplitPane verticalSplitPane = new JSplitPane(
        JSplitPane.VERTICAL_SPLIT,
//        new JScrollPane(historyTable)
        historyTable,
        new JScrollPane(horizontalSplitPane)
    );

    verticalSplitPane.setContinuousLayout(true);
    verticalSplitPane.setDividerLocation(500);
    verticalSplitPane.setDividerSize(1);

    return verticalSplitPane;
  }

  private JPanel createButtonsPanel() {
    JButton buttonAddNewTab = new JButton("Add New Tab");
    buttonAddNewTab.addActionListener(e -> addTab());

    JButton buttonCreateFullHistoryTab = new JButton("Create Full History");
    buttonCreateFullHistoryTab.addActionListener(e -> addFullHistoryToNewTab());

    JButton buttonDeleteTab = new JButton("Delete Tab");
    buttonDeleteTab.addActionListener(e -> removeCurrentlyActiveTab());

    JButton saveButton = new JButton("Save Current");
    saveButton.addActionListener(e -> logic.saveCurrentFlow());

    JButton saveAllButton = new JButton("Save All");
    saveAllButton.addActionListener(e -> logic.saveAllFlows());

    JButton loadButton = new JButton("Load");
    loadButton.addActionListener(e -> logic.loadSpecificFlow() );

    JButton loadAllButton = new JButton("Load All");
    loadAllButton.addActionListener(e -> logic.loadAllFlows());

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

    JLabel separator1 = new JLabel("  -  ");
    JLabel separator2 = new JLabel("  -  ");

    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    buttonsPanel.add(buttonAddNewTab);
    buttonsPanel.add(buttonDeleteTab);
    buttonsPanel.add(buttonCreateFullHistoryTab);

    buttonsPanel.add(separator1);

    buttonsPanel.add(loadButton);
    buttonsPanel.add(loadAllButton);
    buttonsPanel.add(saveButton);
    buttonsPanel.add(saveAllButton);

    buttonsPanel.add(separator2);

    buttonsPanel.add(moveSelectedToFlow);
    buttonsPanel.add(setSelectedToActiveFlow);
    buttonsPanel.add(flowSelector);
    buttonsPanel.add(new JLabel("  -  Database: "));
    buttonsPanel.add(dbPath);
    buttonsPanel.add(dbPathButton);
    buttonsPanel.add(renameFlow);

    // TODO: button to show panel with current database structure and if smth is saved or not
    return buttonsPanel;
  }

  private void renameCurrentFlow() {
    this.uiRenameFlow.setVisible();
  }

  // TODO: maybe I can simplify this and not crate a local varaible that is later never used
  private JTabbedPane createTabbedPane() {
    tabbedPane = SharedElements.tabbedPane;
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
