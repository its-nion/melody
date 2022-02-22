package com.lopl.melody.utils.embed;

import com.lopl.melody.utils.json.JsonProperties;

import java.awt.*;

public class EmbedColor {

  public static final Color RED = new Color(248, 78, 106, 255);
  public static final Color BLUE = new Color(88, 199, 235, 255);
  public static final Color GREEN = new Color(116, 196, 118, 255);
  public static final Color ERROR = JsonProperties.getProperties().getErrorColor();
  public static final Color JOIN = JsonProperties.getProperties().getErrorColor();
  public static final Color MOVE = JsonProperties.getProperties().getErrorColor();
}
