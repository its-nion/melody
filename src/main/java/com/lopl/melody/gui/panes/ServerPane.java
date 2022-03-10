package com.lopl.melody.gui.panes;

import javax.swing.*;
import java.awt.*;

public class ServerPane extends JPanel {

    private JTable table = new JTable();

    public ServerPane() {
        super();
        setupLayout();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        //headers for the table
        String[] columns = new String[] {
                "Id", "Name", "Part Time"
        };

        //actual data for the table in a 2d array
        Object[][] data = new Object[][] {
                {1, "Donut", false },
                {2, "Nions Server", false },
                {3, "Melody Emotes", true },
        };

        //create table with data
        JTable table = new JTable(data, columns);
        table.setDefaultEditor(Object.class, null);

        //add the table to the frame
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}
