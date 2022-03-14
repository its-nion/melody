package com.lopl.melody.gui.panes;

import javax.swing.*;
import java.awt.*;

public class PaneManager extends JPanel {

    private final Dimension minimumWidth = new Dimension(250, 0);

    private JTabbedPane tabbedPane = new JTabbedPane();

    public PaneManager() {
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
