package com.lopl.melody.gui;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ResizeManager {

  public final JFrame parentFrame;
  public final JSplitPane splitPane;
  public float dividerPosition;

  public ResizeManager(JFrame parentFrame, JSplitPane splitPane) {
    this.parentFrame = parentFrame;
    this.splitPane = splitPane;
    setUp();
  }

  public void setUp(){
    parentFrame.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        setPosition();
      }
    });
    splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent pce) {
        storePosition();
      }
    });
    SwingUtilities.invokeLater(this::storePosition);
  }

  private void storePosition(){
    int fullWidth = parentFrame.getWidth();
    int position = splitPane.getDividerLocation();
    dividerPosition = (float) position / fullWidth;
  }

  private void setPosition(){
    int fullWidth = parentFrame.getWidth();
    int position = (int) (fullWidth * dividerPosition);
    splitPane.setDividerLocation(position);
  }
}
