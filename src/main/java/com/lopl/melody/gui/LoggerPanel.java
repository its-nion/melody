package com.lopl.melody.gui;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class LoggerPanel extends JPanel {

  public static final String POSITION = BorderLayout.EAST;
  public static final Dimension DEFAULT_DIMENSION = new Dimension(200, 360);

  public LoggerPanel() {
    super();
    setBorder(BorderFactory.createTitledBorder("Logger"));
    setVisible(true);
  }

  public void startLogging(){
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    LogHistory logHistory = new LogHistory();
    logHistory.start();
    logHistory.setOnLogListener(log -> {
      Label messageLabel = new Label(log.getFormattedMessage());
      messageLabel.setAlignment(Label.LEFT);
      messageLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 10));
      messageLabel.setFocusable(false);
      add(messageLabel);
      revalidate();
    });

    Logger logger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
    logger.addAppender(logHistory);

  }

  @Override
  public Dimension getPreferredSize() {
    if (isPreferredSizeSet()) {
      return super.getPreferredSize();
    } else {
      return DEFAULT_DIMENSION;
    }
  }

  public static class LogHistory extends ListAppender<ILoggingEvent>{

    private OnLog onLogCallback;

    public LogHistory() {
      super();
    }

    public void setOnLogListener(OnLog onLogCallback) {
      this.onLogCallback = onLogCallback;
    }

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
      super.append(iLoggingEvent);
      onLogCallback.log(iLoggingEvent);
    }

    public interface OnLog{
      void log(ILoggingEvent log);

    }

  }
}
