package core;

import database.DBVariables;
import database.SQLiteDataSource;
import handlers.SlashCommandHandler;
import listeners.SlashCommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import utils.Config;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.EnumSet;

public class Main
{
    public static void main(String[] args) throws LoginException, InterruptedException, SQLException {
        SQLiteDataSource.getConnection();

        JDA jda = JDABuilder.createLight(Config.TOKEN, EnumSet.noneOf(GatewayIntent.class)) // slash commands don't need any intents
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .enableCache(CacheFlag.VOICE_STATE)
                .setAutoReconnect(true)
                .setStatus(Config.STATUS)
                .addEventListeners(new SlashCommandListener())
                .build();

        jda.awaitReady();

        SlashCommandHandler.updateSlashCommands(jda);

        DBVariables.initiateCurrentServers(jda);
    }
}
