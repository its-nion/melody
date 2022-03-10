package com.lopl.melody.gui.panes;

import com.lopl.melody.gui.BotStatus;

import javax.swing.*;
import java.awt.*;

public class MainPane extends JPanel {

    private final Dimension minimumWidth = new Dimension(250, 0);

    private JTabbedPane tabbedPane = new JTabbedPane();

    public MainPane() {
        super();
        setupLayout();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Create all Child-Panes
        tabbedPane.add("General", new BotStatus());
        tabbedPane.add("Servers", new ServerPane());

        tabbedPane.setMinimumSize(minimumWidth);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
