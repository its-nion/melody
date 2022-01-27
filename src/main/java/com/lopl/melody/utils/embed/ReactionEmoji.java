package com.lopl.melody.utils.embed;

public class ReactionEmoji {
  public static final String LEFT = "<:arrow_left:925172462172373063>";
  public static final String RIGHT = "<:arrow_right:925175845683855360>";
  public static final String PAUSE = "<:pause:925180399678791761>";
  public static final String RESUME = "<:play:925180400077267024>";
  public static final String SHUFFLE = "<:shuffle:925181120482541618>";
  public static final String PLUS = "U+2795";
  public static final String MUSIC = "U+1F3B5";
  public static final String SKIP = "<:skip_right:925180400156938330>";
  public static final String BACKWARDS = "<:skip_left:925180400144371762>";
  public static final String STOP = "<:stop:925181120159571970>";

  public static final String CHECKMARK = "U+2705";
  public static final String YOUTUBE = "<:youtube:925827032485621791>";
  public static final String SPOTIFY = "<:spotify:925826109650665473>";
  public static final String YOUTUBE_LINK = "https://cdn.discordapp.com/emojis/925827032485621791.png?size=96";
  public static final String SPOTIFY_LINK = "https://cdn.discordapp.com/emojis/925826109650665473.png?size=96";

  public static final String ONE = "U+0031";
  public static final String TWO = "U+0032";
  public static final String THREE = "U+0033";
  public static final String FOUR = "U+0034";
  public static final String FIVE = "U+0035";
  public static final String SIX = "U+0036";
  public static final String SEVEN = "U+0037";
  public static final String EIGHT = "U+0038";
  public static final String NINE = "U+0039";
  public static final String TEN = "U+1F51F";

  public static String getNumberAsEmoji(int number) {
    return switch (number) {
      case 1 -> ":one:";
      case 2 -> ":two:";
      case 3 -> ":three:";
      case 4 -> ":four:";
      case 5 -> ":five:";
      case 6 -> ":six:";
      case 7 -> ":seven:";
      case 8 -> ":eight:";
      case 9 -> ":nine:";
      case 10 -> ":keycap_ten:";
      default -> ":stop_button:";
    };
  }

}
