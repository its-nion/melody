package com.lopl.melody;

import com.lopl.melody.slash.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import com.lopl.melody.utils.Logging;

import javax.security.auth.login.LoginException;

public class Melody
{
    public static JDA manager;

    private Melody() throws LoginException, InterruptedException {
        setup();
        Logging.info(getClass(), null, null, "Loaded! Melody is now ready.");
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        new Melody();
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
        SlashCommandClient slashCommandClient = slashCommandClientBuilder.build();
        builder.addEventListeners(new SlashCommandHandler(), slashCommandClient);

        Melody.manager = builder.build();
        Melody.manager.awaitReady();

        //TODO: DataBase
//        DBVariables.initiateCurrentServers(Main.manager);
    }
}
