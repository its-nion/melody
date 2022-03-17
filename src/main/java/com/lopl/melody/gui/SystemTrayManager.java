package com.lopl.melody.gui;

import com.lopl.melody.utils.Logging;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SystemTrayManager {

  public final JFrame parentFrame;

  public SystemTrayManager(JFrame parentFrame) {
    this.parentFrame = parentFrame;
    try {
      systemTraySetup();
    } catch (AWTException e) {
      Logging.info(getClass(), null, null, "Error when initializing System tray");
    }
  }

  private void systemTraySetup() throws AWTException {
    String url = System.getProperty("user.dir") + "\\build\\resources\\main\\bitmap\\melody_transparent.png";
    ImageIcon image = new ImageIcon(url);

    if (SystemTray.isSupported()) {
      parentFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
      final SystemTray tray = SystemTray.getSystemTray();
      final PopupMenu popup = new PopupMenu();
      final TrayIcon icon   = new TrayIcon(image.getImage(), "Melody", popup);
      icon.setImageAutoSize(true);
      icon.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          if (e.getButton() == MouseEvent.BUTTON1) {
            parentFrame.setVisible(true);
          }
        }
      });

      tray.add(icon);

      MenuItem item1 = new MenuItem("Open Melody");
      item1.addActionListener(e -> {
        parentFrame.setVisible(true);
        parentFrame.setLocationRelativeTo(null);
      });
      MenuItem item2 = new MenuItem("Quit");
      item2.addActionListener(e -> {
        tray.remove(icon);
        parentFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        parentFrame.dispose();
        //System.exit(0);
      });
      popup.add(item1);
      popup.add(item2);
    }
  }


}
