package com.lopl.melody.gui.panes;

import javax.swing.*;
import java.awt.*;

public class PaneManager extends JPanel {

    private final Dimension minimumWidth = new Dimension(250, 0);
    private final JTabbedPane tabbedPane = new JTabbedPane();

    public final BotStatusPane botStatus;
    public final ServerPane serverPane;
    public final PropertiesPane propertiesPane;


    public PaneManager() {
        super();
        botStatus = new BotStatusPane(this);
        serverPane = new ServerPane(this);
        propertiesPane = new PropertiesPane(this);
        setupLayout();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Create all Child-Panes
        tabbedPane.add("General", botStatus);
        tabbedPane.add("Servers", serverPane);
        tabbedPane.add("Properties", propertiesPane);
        tabbedPane.setMinimumSize(minimumWidth);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
