package com.lopl.melody;

import com.lopl.melody.audio.util.BotRightsManager;
import com.lopl.melody.slash.*;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.embed.EmojiGuildManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.List;

public class Melody {
  public static JDA manager;

  public static void main(String[] args) throws InterruptedException {
    new Melody();
  }

  private Melody() throws InterruptedException {
    try {
      setup();
    } catch (LoginException e) {
      Logging.error(Melody.class, null, null, "Bot not registered! Head to https://discord.com/developers/applications to register the bot. Open the properties.json to set the bots key afterwards.");
    }
  }

  private void setup() throws LoginException, InterruptedException {
    JDABuilder builder = JDABuilder.createDefault(Token.BOT_TOKEN);
    builder.setActivity(Activity.of(Activity.ActivityType.LISTENING, "Music"));
    builder.setBulkDeleteSplittingEnabled(false);
    builder.setCompression(Compression.NONE);
    builder.enableIntents(GatewayIntent.GUILD_VOICE_STATES);
    builder.enableCache(CacheFlag.VOICE_STATE);
    builder.setAutoReconnect(true);
    builder.setStatus(OnlineStatus.ONLINE);

    SlashCommandClientBuilder slashCommandClientBuilder = new SlashCommandClientBuilder();
    slashCommandClientBuilder.addCommands(SlashCommands.getCommands().toArray(SlashCommand[]::new));
    slashCommandClientBuilder.addEventListeners(builder, SlashCommands.getCommands().toArray(SlashCommand[]::new));
    SlashCommandClient slashCommandClient = slashCommandClientBuilder.build();
    slashCommandClient.start();

    builder.addEventListeners(new SlashCommandHandler(), slashCommandClient);
    builder.addEventListeners(new BotRightsManager());

    Melody.manager = builder.build();
    Logging.info(getClass(), null, null, "Loaded! Melody is now ready.");
    slashCommandClient.ready(Melody.manager);
    Melody.manager.awaitReady();
    List<Guild> guilds = Melody.manager.getGuilds();
    EmojiGuildManager.loadAllEmotes(guilds);
  }
}
