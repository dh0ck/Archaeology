package com.antlarac;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import com.antlarac.Database.*;

public class Ui {
  JTabbedPane tabbedPane;
  Logic logic = new Logic();
  Database db = new Database();
  private final MontoyaApi api;
  private final Logging logging;
  JComboBox<String> flowSelector = new JComboBox<>();

  public Ui(MontoyaApi api, Logging logging) throws ClassNotFoundException, SQLException {
    this.api = api;
    this.logging = logging;
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

  private JTable createHistoryTable(List<ProxyHttpRequestResponse> historyList) {
    DefaultTableModel tableModel = createTableModel();
    logging.logToOutput("full history");
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

    return historyTable;
  }

  private JTextArea createRequestTextArea() {
    JTextArea requestTextArea = new JTextArea();

    return requestTextArea;
  }

  private JTextArea createResponseTextArea() {
    JTextArea responseTextArea = new JTextArea();

    return responseTextArea;
  }

  private JSplitPane createHorizontalSplitPane() {
    JSplitPane horizontalSplitPane = new JSplitPane(
        JSplitPane.HORIZONTAL_SPLIT,
        new JScrollPane(this.createRequestTextArea()),
        new JScrollPane(createResponseTextArea())
    );
    horizontalSplitPane.setContinuousLayout(true);
    horizontalSplitPane.setDividerLocation(1000);
    horizontalSplitPane.setDividerSize(1);

    return horizontalSplitPane;
  }

  private JSplitPane createVerticalSplitPane(List<ProxyHttpRequestResponse> historyList) {
    JSplitPane verticalSplitPane = new JSplitPane(
        JSplitPane.VERTICAL_SPLIT,
        new JScrollPane(createHistoryTable(historyList)),
        new JScrollPane(createHorizontalSplitPane())
    );

    verticalSplitPane.setContinuousLayout(true);
    verticalSplitPane.setDividerLocation(500);
    verticalSplitPane.setDividerSize(1);

    return verticalSplitPane;
  }

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

    JButton loadButton = new JButton("Load");

    updateFlowSelectorComboBox();
    JButton moveSelectedToFlow = new JButton("Move Selected to Flow");

    JButton setSelectedToActiveFlow = new JButton("Set selected Flow as active");

    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    JButton createDB = new JButton("Create DB");
//    createDB.addActionListener(e -> Database());
    // test
    buttonsPanel.add(buttonAddNewTab);
    buttonsPanel.add(buttonDeleteTab);
    buttonsPanel.add(buttonCreateFullHistoryTab);
    buttonsPanel.add(loadButton);
    buttonsPanel.add(saveButton);
    buttonsPanel.add(moveSelectedToFlow);
    buttonsPanel.add(setSelectedToActiveFlow);
    buttonsPanel.add(this.flowSelector);
    return buttonsPanel;
  }

  private JTabbedPane createTabbedPane() {
    tabbedPane = new JTabbedPane();
    tabbedPane.addTab("Flow 1", createVerticalSplitPane(List.of()));
    updateFlowSelectorComboBox();
    return tabbedPane;
  }

  private void addTab() {
    int numTabs = tabbedPane.getTabCount();
    tabbedPane.addTab("New Flow", createVerticalSplitPane(List.of()));
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
    tabbedPane.addTab("Full History", createVerticalSplitPane(logic.getFullHistory(this.api)));
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
