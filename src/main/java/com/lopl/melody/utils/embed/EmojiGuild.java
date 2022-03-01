package com.lopl.melody.utils.embed;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class EmojiGuild{

  private static final long MELODY_EMOTES_GUILD_ID = 947457007739351042L;
  public final JDA jda;
  public final long id;
  private Guild guild;

  public EmojiGuild(JDA jda){
    this.jda = jda;
    this.id = MELODY_EMOTES_GUILD_ID;
  }

  public Guild getGuild(){
    if (guild != null) return guild;
    return loadGuild();
  }

  private Guild loadGuild() {
    return jda.getGuildById(id);
  }
}
