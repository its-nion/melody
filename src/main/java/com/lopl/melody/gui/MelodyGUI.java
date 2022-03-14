package com.lopl.melody.gui;

import com.lopl.melody.gui.panes.LoggerPane;
import com.lopl.melody.gui.panes.PaneManager;

import javax.swing.*;

public class MelodyGUI {

  public static void main(String[] args) {
    new MelodyGUI();
  }

  private MelodyGUI() {
    createAndShowGUI();
  }

  public void createAndShowGUI() {
    String url = System.getProperty("user.dir") + "\\build\\resources\\main\\bitmap\\melody_transparent.png";
    ImageIcon image = new ImageIcon(url);

    // General Window Settings
    final JFrame frame = new JFrame("Melody");
    frame.setIconImage(image.getImage());
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setResizable(true);
    frame.setSize(640, 360);
    frame.setLocationRelativeTo(null);

    // Layout
    LoggerPane loggerPane = new LoggerPane();
    PaneManager paneManager = new PaneManager();

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, paneManager, loggerPane);
    splitPane.setDividerLocation(.7);

    frame.add(splitPane);

    // If OS does support SystemTray
    SystemTrayManager systemTrayManager = new SystemTrayManager(frame);

    // Makes window visible
    frame.setVisible(true);
  }

}
