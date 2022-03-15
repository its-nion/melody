package com.lopl.melody.gui.panes;

import com.lopl.melody.utils.json.JsonProperties;
import com.lopl.melody.utils.json.PropertiesData;

import javax.swing.*;
import java.awt.*;

public class PropertiesPane extends JPanel {

  private final PaneManager manager;

  public PropertiesPane(PaneManager manager) {
    this.manager = manager;
    setupLayout();
  }

  private void setupLayout() {
    PropertiesData data = JsonProperties.getData();
    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints layoutConstraints = new GridBagConstraints();
    layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
    layoutConstraints.weighty = 0;
    layoutConstraints.weightx = 0;
    layoutConstraints.anchor = GridBagConstraints.NORTH;
    layoutConstraints.insets = new Insets(4,4,2,4);  //top padding
    layoutConstraints.gridx = 0;
    setLayout(layout);
    setAlignmentY(0);

    // adding labels with increasing y coordinate
    layoutConstraints.gridy = 0;
    add(new Label("Bot Key:"), layoutConstraints);

    layoutConstraints.gridx = 1;
    layoutConstraints.weightx = 1;

    // adding input fields with increasing y coordinate
    layoutConstraints.gridy = 0;
    add(new TextField(data.botKey), layoutConstraints);

    // empty space below
    layoutConstraints.weightx = 0;
    layoutConstraints.gridy = 2;
    layoutConstraints.gridx = 0;
    layoutConstraints.weighty = 1;
    add(new Label(""), layoutConstraints);
    setVisible(true);
  }

}
