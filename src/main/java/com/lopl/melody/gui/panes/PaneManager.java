package com.lopl.melody.gui.panes;

import javax.swing.*;
import java.awt.*;

public class PaneManager extends JPanel {

    private final Dimension minimumWidth = new Dimension(250, 0);
    public final BotStatus botStatus;
    public final ServerPane serverPane;

    private JTabbedPane tabbedPane = new JTabbedPane();

    public PaneManager() {
        super();
        botStatus = new BotStatus(this);
        serverPane = new ServerPane(this);
        setupLayout();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Create all Child-Panes
        tabbedPane.add("General", botStatus);
        tabbedPane.add("Servers", serverPane);
        tabbedPane.setMinimumSize(minimumWidth);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
