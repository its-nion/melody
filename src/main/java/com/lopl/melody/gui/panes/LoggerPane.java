package com.lopl.melody.gui.panes;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.InlineView;
import javax.swing.text.html.ParagraphView;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

public class LoggerPane extends JPanel implements HyperlinkListener {

  private final Dimension minimumWidth = new Dimension(200, 0);

  private final JTextPane textPane = new JTextPane();
  private final JScrollPane scrollPane = new JScrollPane(textPane);
  private final HTMLDocument doc = new HTMLDocument();
  private final HTMLEditorKit htmlEditorKit = getHtmlEditorKit();

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
    textPane.setBackground(Color.darkGray);
    textPane.addHyperlinkListener(this);
    scrollPane.setMinimumSize(minimumWidth);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    add(scrollPane, BorderLayout.CENTER);
    printHtmlStringToConsole("<b><font size='5' color='white'>Melody v1.0</font></b><br>");

  }

  public void printHtmlStringToConsole(String text) {
    try {
      htmlEditorKit.insertHTML(doc, doc.getLength(), text, 0, 0, null);
    } catch (IOException | BadLocationException e) {
      try {
        throw e;
      } catch (IOException | BadLocationException ex) {
        ex.printStackTrace();
      }
    }
  }

  public void startLogging() {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    LogHistory logHistory = new LogHistory();
    logHistory.start();
    logHistory.setOnLogListener(log -> {
      String message = log.getFormattedMessage();

      // look for urls:
      String messageWithURL = "";
      for (String word : message.split(" "))
        if (Pattern.matches("(http(s)?://.)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)", word))
          messageWithURL = message.replace(word, "<font color='blue'><a href=\"%s\">%s</a></font>".formatted(word, word));
      if (!messageWithURL.isEmpty() && !message.equals(messageWithURL))
        message = messageWithURL;

      // looking for guilds and members:
      String messageWithGuild = "";
      String[] splits = message.split("]");
      if (splits.length >= 2 && splits[0].contains("[")){
        String guildName = splits[0].replace("[", "");
        messageWithGuild = message.replace(guildName, "<font color='yellow'>" + guildName + "</font>");
      }
      if (splits.length >= 3 && splits[1].contains("[")){
        String memberName = splits[1].replace("[", "");
        messageWithGuild = messageWithGuild.replace(memberName, "<font color='yellow'>" + memberName + "</font>");

      }
      if (!messageWithGuild.isEmpty() && !message.equals(messageWithGuild))
        message = messageWithGuild;

      // giving the text some color
      String color = log.getLevel().isGreaterOrEqual(Level.WARN) ? "red" : "white";
      String output = "<font color='" + color + "'>" + message + "</font>";
      printHtmlStringToConsole(output);
      revalidate();

      // Scrolls down to latest message
      SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()));
    });

    Logger logger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
//    logger.setLevel(Level.INFO);
    logger.addAppender(logHistory);
  }

  @Override
  public void hyperlinkUpdate(HyperlinkEvent e) {
    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      if (Desktop.isDesktopSupported()) {
        try {
          Desktop.getDesktop().browse(e.getURL().toURI());
        } catch (IOException | URISyntaxException e1) {
          e1.printStackTrace();
        }
      }
    }
  }

  public static class LogHistory extends ListAppender<ILoggingEvent> {
    private OnLog onLogCallback;

    public LogHistory() {
      super();
      addFilter(new Filter<>() {
        @Override
        public FilterReply decide(ILoggingEvent event) {
          return event.getLevel().isGreaterOrEqual(Level.INFO) ? FilterReply.ACCEPT : FilterReply.DENY;
        }
      });
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

    public interface OnLog {
      void log(ILoggingEvent log) throws BadLocationException, IOException;
    }
  }

  private HTMLEditorKit getHtmlEditorKit(){
    return new HTMLEditorKit(){
      @Override
      public ViewFactory getViewFactory() {
        return new HTMLFactory(){
          public View create(Element e){
            View v = super.create(e);
            if(v instanceof InlineView){
              return new InlineView(e){
                public int getBreakWeight(int axis, float pos, float len) {
                  return GoodBreakWeight;
                }
                public View breakView(int axis, int p0, float pos, float len) {
                  if(axis == View.X_AXIS) {
                    checkPainter();
                    int p1 = getGlyphPainter().getBoundedPosition(this, p0, pos, len);
                    if(p0 == getStartOffset() && p1 == getEndOffset()) {
                      return this;
                    }
                    return createFragment(p0, p1);
                  }
                  return this;
                }
              };
            }
            else if (v instanceof ParagraphView) {
              return new ParagraphView(e) {
                protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) {
                  if (r == null) {
                    r = new SizeRequirements();
                  }
                  float pref = layoutPool.getPreferredSpan(axis);
                  float min = layoutPool.getMinimumSpan(axis);
                  // Don't include insets, Box.getXXXSpan will include them.
                  r.minimum = (int)min;
                  r.preferred = Math.max(r.minimum, (int) pref);
                  r.maximum = Integer.MAX_VALUE;
                  r.alignment = 0.5f;
                  return r;
                }

              };
            }
            return v;
          }
        };
      }
    };
  }
}
