package com.antlarac.UiElements;

import javax.swing.*;
import java.util.Objects;

public class SharedElements {
    public static JTabbedPane tabbedPane = new JTabbedPane();

    public static JComboBox<String> flowSelector = new JComboBox<>();

    public static void updateFlowSelectorComboBox() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        if (Objects.isNull(tabbedPane)) {
            model.addElement("");
        } else {
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                model.addElement(tabbedPane.getTitleAt(i));
            }
        }
        SharedElements.flowSelector.setModel(model);
    }
}
