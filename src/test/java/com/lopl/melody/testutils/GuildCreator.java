package com.lopl.melody.testutils;

import com.lopl.melody.Token;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.utils.config.AuthorizationConfig;
import org.jetbrains.annotations.TestOnly;

public class GuildCreator {

  @TestOnly
  public static Guild create(long id) {
    JDAImpl jda = new JDAImpl(new AuthorizationConfig(Token.BOT_TOKEN));
    return new GuildImpl(jda, id);
  }

}
