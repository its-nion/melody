package com.lopl.melody.utils.embed;

import com.lopl.melody.Melody;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.json.JsonProperties;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ListedEmote;
import net.dv8tion.jda.api.entities.Member;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class EmojiGuildManager {

  private EmojiGuild emojiGuild;

  private static final List<String> emoteIDs = List.of(
    "icon"
  );

  public EmojiGuildManager withGuild(EmojiGuild guild){
    this.emojiGuild = guild;
    return this;
  }

  public boolean isAvailable() {
    JDA jda = emojiGuild.jda;
    Guild guild = jda.getGuildById(emojiGuild.id);
    if (guild != null){
      //TODO check for permissions

      return true;
    }
    long clientId = jda.getSelfUser().getApplicationIdLong();
    long scope = getPermissionValueEmoteServer();
    long guildId = emojiGuild.id;
    String url = String.format("https://discord.com/api/oauth2/authorize?client_id=%d&scope=bot&permissions=%d&guild_id=%d&disable_guild_select=true", clientId, scope, guildId);
    Logging.error(Melody.class, null, null, "Please join the Emote server with the invite: https://discord.gg/h9g8Gezuet");
    Logging.error(Melody.class, null, null, "Head to the following url to register the bot for the required Emote-Server: " + url);
    return false;
  }

  public void loadEmotes(){
    Guild guild = emojiGuild.getGuild();
    if (guild == null) return;
    List<ListedEmote> emotes = guild.retrieveEmotes().complete();
    for (Emote emote : emotes){
      try {
        String emoteName = emote.getName();
        String varName = emoteName.toUpperCase();
        String emoteMarkdown = emote.getAsMention();
        Field field = ReactionEmoji.class.getDeclaredField(varName);
        field.set(ReactionEmoji.class, emoteMarkdown);
      } catch (NoSuchFieldException | IllegalAccessException ignored) {
        Logging.debug(getClass(), guild, null, "Failed to load emote: :" + emote.getName() + ":");
      }
    }

  }

  private long getPermissionValueEmoteServer(){
    return Permission.getRaw(
        Permission.MESSAGE_READ,
        Permission.VIEW_CHANNEL,
        Permission.MESSAGE_MANAGE,
        Permission.MESSAGE_EXT_EMOJI,
        Permission.MESSAGE_HISTORY,
        Permission.MANAGE_EMOTES
    );
  }

}
