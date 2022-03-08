package com.lopl.melody.gui;

import javax.swing.*;
import java.awt.*;

public class LoggerPanel extends JPanel {

  public static final String POSITION = BorderLayout.EAST;
  public static final Dimension DEFAULT_DIMENSION = new Dimension(200, 360);

  public LoggerPanel() {
    super();
    setBorder(BorderFactory.createTitledBorder("Logger"));
  }



  @Override
  public Dimension getPreferredSize() {
    if (isPreferredSizeSet()) {
      return super.getPreferredSize();
    } else {
      return DEFAULT_DIMENSION;
    }
  }
}
