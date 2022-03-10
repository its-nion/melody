package com.lopl.melody.gui.panes;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class LoggerPane extends JPanel {

  private final Dimension minimumWidth = new Dimension(200, 0);

  private JTextPane textPane = new JTextPane();
  private JScrollPane scrollPane = new JScrollPane(textPane);

  private HTMLDocument doc = new HTMLDocument();
  private HTMLEditorKit htmlEditorKit = new HTMLEditorKit();

  public LoggerPane() {
    super();
    setupLayout();
    startLogging();
  }

  private void setupLayout() {
    setLayout(new BorderLayout());

    textPane.setEditable(false);
    textPane.setContentType("text/html");
    textPane.setEditorKit(htmlEditorKit);
    textPane.setDocument(doc);
    textPane.setBackground(Color.black);

    printHtmlStringToConsole("<b><font size='5' color='white'>Melody v1.0</font></b><br>");

    scrollPane.setMinimumSize(minimumWidth);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    add(scrollPane, BorderLayout.CENTER);

    // Listens for presses on html hyperlinks
    textPane.addHyperlinkListener(new HyperlinkListener() {
                                    public void hyperlinkUpdate(HyperlinkEvent e) {
                                      if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                                        if(Desktop.isDesktopSupported()) {
                                          try {
                                            Desktop.getDesktop().browse(e.getURL().toURI());
                                          }
                                          catch (IOException | URISyntaxException e1) {
                                            e1.printStackTrace();
                                          }
                                        }
                                      }
                                    }
                                  }
    );
  }

  public void printHtmlStringToConsole(String text) {
    try {
      htmlEditorKit.insertHTML(doc, doc.getLength(), text, 0, 0, null);
    } catch (IOException | BadLocationException e) {
      try {
        throw e;
      } catch (IOException ex) {
        ex.printStackTrace();
      } catch (BadLocationException ex) {
        ex.printStackTrace();
      }
    }
  }

  public void startLogging(){
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    LogHistory logHistory = new LogHistory();
    logHistory.start();
    logHistory.setOnLogListener(log -> {
      String output = "<font color='white'>" + log.getFormattedMessage() + "</font>";
      htmlEditorKit.insertHTML(doc, doc.getLength(),output, 0, 0,null);
      revalidate();

      // Scrolls down to latest message
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          scrollPane.getVerticalScrollBar().setValue( scrollPane.getVerticalScrollBar().getMaximum() );
        }
      });
    });

    Logger logger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
    logger.addAppender(logHistory);
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
      try {
        onLogCallback.log(iLoggingEvent);
      } catch (BadLocationException | IOException e) {
        e.printStackTrace();
      }
    }

    public interface OnLog{
      void log(ILoggingEvent log) throws BadLocationException, IOException;
    }
  }
}
