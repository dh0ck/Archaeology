package com.antlarac.UiElements;

import javax.swing.*;
import java.awt.*;

public class UiRenameFlow {
    JTextField newName = new JTextField();
    JFrame frame;
    JPanel panel;

    public UiRenameFlow() {
        this.frame = new JFrame("Rename Flow");
        this.panel = new JPanel();
        this.populatePanel();
        this.frame.add(panel);
        this.frame.setSize(300, 100);
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(false);
        this.newName = newName ;
    }

    public void setVisible() {
       this.frame.setVisible(true);
    }

    public void setInVisible() {
        this.frame.setVisible(false);
    }

    private void renameTab() {
        int selectedIndex = SharedTabbedPane.tabbedPane.getSelectedIndex();
        SharedTabbedPane.tabbedPane.setTitleAt(selectedIndex, this.newName.getText());
        this.setInVisible();
        this.newName.setText("");
        //TODO: update combo box: extract updateFlowSelectorComboBox to shared class (like SharedTabbedPane.java)
        // to be able to call it from both UIs
    }

    private void populatePanel() {
        int rows = 3;
        int cols = 2;
        GridLayout layout = new GridLayout(rows, cols);
        this.panel.setLayout(layout);

        JPanel textPanel = new JPanel();
        textPanel.setLayout((new BoxLayout(textPanel, BoxLayout.X_AXIS)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout((new BoxLayout(buttonPanel, BoxLayout.X_AXIS)));

        JLabel titleLabel = new JLabel("  Rename Flow   ");
        JLabel postLabel = new JLabel("   ");
        JButton rename = new JButton("Rename");
        rename.addActionListener(e -> renameTab());
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> setInVisible());

        textPanel.add(titleLabel);
        textPanel.add(this.newName);
        textPanel.add(postLabel);

        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(rename);
        buttonPanel.add(cancel);
        buttonPanel.add(Box.createHorizontalGlue());

        this.panel.add(textPanel);
        this.panel.add(buttonPanel);
    }


}
