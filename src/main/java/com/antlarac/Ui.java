package com.antlarac;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
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


  public JPanel createUi() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(createVerticalSplitPane(), BorderLayout.CENTER);
    return panel;
  }
}
