package com.lopl.melody.utils.embed;

import com.lopl.melody.utils.annotation.CustomEmote;

/**
 * This class contains all static references to all emojis
 * Some of these references may be overwritten by the {@link EmojiGuildManager}
 */
public class ReactionEmoji {

  @CustomEmote
  public static String ICON = "U+1F3B5"; // "<:icon:12345678901234567890>";

  @CustomEmote
  public static String ARROW_LEFT = "U+2B05"; // ""<:arrow_left:925172462172373063>";
  @CustomEmote
  public static String ARROW_RIGHT = "U+27A1"; // "<:arrow_right:925175845683855360>";
  @CustomEmote
  public static String PAUSE = "U+2016"; // "<:pause:925180399678791761>";
  @CustomEmote
  public static String PLAY = "U+25B6"; // "<:play:925180400077267024>";
  @CustomEmote
  public static String SHUFFLE = "U+1F500"; // "<:shuffle:925181120482541618>";
  @CustomEmote
  public static String SHUFFLE_ACTIVE = "U+23E9";
  @CustomEmote
  public static String PLUS = "U+2795";
  @CustomEmote
  public static String MUSIC = "U+1F3B5";
  @CustomEmote
  public static String SKIP_RIGHT = "U+23ED"; // "<:skip_right:925180400156938330>";
  @CustomEmote
  public static String SKIP_LEFT = "U+23EE"; // "<:skip_left:925180400144371762>";
  @CustomEmote
  public static String STOP = "U+23F9"; // "<:stop:925181120159571970>";
  @CustomEmote
  public static String CHECKMARK = "U+2705";
  @CustomEmote
  public static String REPEAT = "U+1F501";
  @CustomEmote
  public static String REPEAT_ACTIVE = "U+23E9";

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
   *
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
