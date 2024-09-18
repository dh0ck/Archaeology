package com.antlarac;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UiRenameFlow {
    JFrame frame;
    JPanel panel;

    UiRenameFlow() {
        this.frame = new JFrame("Rename Flow");
        this.panel = new JPanel();
        this.populatePanel();
        this.frame.add(panel);
        this.frame.setSize(500, 200);
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(false);
    }

    public void setVisible() {
       this.frame.setVisible(true);
    }

    public void setInVisible() {
        this.frame.setVisible(false);
    }

    private void populatePanel() {
        JButton rename = new JButton("Rename");
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> setInVisible());
        this.panel.add(rename);
        this.panel.add(cancel);
    }


}
