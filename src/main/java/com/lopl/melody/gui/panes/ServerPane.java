package com.lopl.melody.gui.panes;

import com.lopl.melody.Melody;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.Vector;

public class ServerPane extends JPanel {

  private final PaneManager manager;
  private JTable table;
  private Object[][] data;

  public ServerPane(PaneManager manager) {
    super();
    this.manager = manager;
    setupLayout();
  }

  public void reloadData() {
    if (Melody.manager == null || Melody.manager.getStatus() != JDA.Status.CONNECTED) return;

    Guild[] guilds = Melody.manager.getGuilds().toArray(Guild[]::new);
    data = new Object[guilds.length][2];
    for (int i = 0; i < guilds.length; i++) {
      Guild guild = guilds[i];
      data[i][0] = i + 1;
      data[i][1] = guild.getName();
    }
    updateTable();
  }

  private void updateTable() {
    DefaultTableModel model = ((DefaultTableModel) table.getModel());
    while (model.getRowCount() > 0){
      model.removeRow(0);
    }
    for (Object[] rowData : data) {
      model.addRow(rowData);
    }
    table.setModel(model);
    table.updateUI();
  }

  private void setupLayout() {
    setLayout(new BorderLayout());

    //create table
    table = new JTable(new DefaultTableModel(/*new Object[]{"ID", "Servername"}, 3*/));
    ((DefaultTableModel) table.getModel()).addColumn("ID");
    ((DefaultTableModel) table.getModel()).addColumn("Servername");
    table.setDefaultEditor(Object.class, null);
    table.setFillsViewportHeight(true);

    //add the table to the frame
    add(new JScrollPane(table), BorderLayout.CENTER);
  }
}
