package com.lopl.melody.utils.embed;

/**
 * This class contains all static references to all emojis
 * Some of these references may be overwritten by the {@link EmojiGuildManager}
 */
public class ReactionEmoji {
  public static String ICON = "<:icon:12345678901234567890>";

  public static String ARROW_LEFT = "<:arrow_left:925172462172373063>";
  public static String ARROW_RIGHT = "<:arrow_right:925175845683855360>";
  public static String PAUSE = "<:pause:925180399678791761>";
  public static String PLAY = "<:play:925180400077267024>";
  public static String SHUFFLE = "<:shuffle:925181120482541618>";
  public static String SHUFFLE_ACTIVE = "U+1F7E6";
  public static String PLUS = "U+2795";
  public static String MUSIC = "U+1F3B5";
  public static String SKIP_RIGHT = "<:skip_right:925180400156938330>";
  public static String SKIP_LEFT = "<:skip_left:925180400144371762>";
  public static String STOP = "<:stop:925181120159571970>";
  public static String CHECKMARK = "U+2705";
  public static String REPEAT = "U+1F501";
  public static String REPEAT_ACTIVE = "U+1F7E6";

  public static String YOUTUBE = "<:youtube:925827032485621791>";
  public static String SPOTIFY = "<:spotify:925826109650665473>";
  public static String YOUTUBE_LINK = "https://cdn.discordapp.com/emojis/925827032485621791.png?size=96";
  public static String SPOTIFY_LINK = "https://cdn.discordapp.com/emojis/925826109650665473.png?size=96";

  public static String ONE = "U+0031";
  public static String TWO = "U+0032";
  public static String THREE = "U+0033";
  public static String FOUR = "U+0034";
  public static String FIVE = "U+0035";
  public static String SIX = "U+0036";
  public static String SEVEN = "U+0037";
  public static String EIGHT = "U+0038";
  public static String NINE = "U+0039";
  public static String TEN = "U+1F51F";

  /**
   * This returns the number emoji from a passed int.
   * supported are [1, 10] everything else returns the square emoji
   * @param number any int
   * @return a Number emoji
   */
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
