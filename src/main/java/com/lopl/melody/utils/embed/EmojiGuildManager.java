package com.lopl.melody.utils.embed;

import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.annotation.CustomEmote;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ListedEmote;

import java.lang.reflect.Field;
import java.util.List;

public class EmojiGuildManager {

  public static void loadAllEmotes(List<Guild> guilds){
    int loadCount = 0;
    for (Guild guild : guilds)
      loadCount += loadEmotes(guild);
    Logging.debug(EmojiGuildManager.class, null, null, "Found " + loadCount + " custom emotes");
  }

  public static int loadEmotes(Guild guild){
    int loadCount = 0;
    if (guild == null) return 0;
    List<ListedEmote> emotes = guild.retrieveEmotes().complete();
    for (Emote emote : emotes){
      try {
        String emoteName = emote.getName();
        String varName = emoteName.toUpperCase();
        String emoteMarkdown = emote.getAsMention();
        Field field = ReactionEmoji.class.getDeclaredField(varName);
        if (!field.isAnnotationPresent(CustomEmote.class))
          continue;
        field.set(ReactionEmoji.class, emoteMarkdown);
        loadCount++;
      } catch (NoSuchFieldException | IllegalAccessException ignored) {
      }
    }
    return loadCount;
  }

}
