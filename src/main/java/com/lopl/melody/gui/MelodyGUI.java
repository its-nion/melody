package com.lopl.melody.gui;

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

    // General Settings
    final JFrame frame = new JFrame("Melody");
    frame.setIconImage(image.getImage());
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setResizable(true);
    frame.setSize(640, 360);
    frame.setLocationRelativeTo(null);

    // layout
    LoggerPanel loggerPanel = new LoggerPanel();
    frame.add(loggerPanel, LoggerPanel.POSITION);

    BotStatus botStatus = new BotStatus();
    frame.add(botStatus);

    // If OS does support SystemTray
    SystemTrayManager systemTrayManager = new SystemTrayManager(frame);

    // Makes window visible
    frame.setVisible(true);
  }

}
