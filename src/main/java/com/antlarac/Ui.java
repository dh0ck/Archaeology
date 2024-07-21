package com.antlarac;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

public class Ui {

  private DefaultTableModel createTableModel() {
    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.addColumn("Name");
    tableModel.addColumn("Age");

    return tableModel;
  }

  private JTable createHistoryTable() {
    DefaultTableModel tableModel = createTableModel();
    tableModel.addRow(new Object[]{"Ankara", "23"});
    tableModel.addRow(new Object[]{"lsadf", "2343"});
    tableModel.addRow(new Object[]{"Anjashfdsadkara", "231123"});
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

  private JSplitPane createVerticalSplitPane() {
    JSplitPane verticalSplitPane = new JSplitPane(
        JSplitPane.VERTICAL_SPLIT,
        new JScrollPane(createHistoryTable()),
        new JScrollPane(createHorizontalSplitPane())
    );

    verticalSplitPane.setContinuousLayout(true);
    verticalSplitPane.setDividerLocation(500);
    verticalSplitPane.setDividerSize(1);

    return verticalSplitPane;
  }

  private JPanel createButtonsPanel() {
    JButton buttonAddNewTab = new JButton("Add New Tab");
    JButton saveButton = new JButton("Save");
    JButton loadButton = new JButton("Load");
    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    buttonsPanel.add(buttonAddNewTab);
    buttonsPanel.add(loadButton);
    buttonsPanel.add(saveButton);
    return buttonsPanel;
  }

  public JPanel createUi() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(createVerticalSplitPane(), BorderLayout.CENTER);
//    return panel;

    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab("Flow 1", createVerticalSplitPane());
    tabbedPane.addTab("Flow 2", createVerticalSplitPane());

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(createButtonsPanel(), BorderLayout.NORTH);
    mainPanel.add(tabbedPane, BorderLayout.CENTER);
    return mainPanel;
  }
}
