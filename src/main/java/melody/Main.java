package melody;

import audioCore.slash.SlashCommand;
import audioCore.slash.SlashCommandClient;
import audioCore.slash.SlashCommandClientBuilder;
import audioCore.slash.SlashCommandHandler;
import com.jagrosh.jdautilities.command.CommandEvent;
import database.DBVariables;
import audioCore.slash.SlashCommands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

public class Main
{
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static JDA manager;

    private Main() throws LoginException, InterruptedException {
        setup();
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        new Main();
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
        slashCommandClientBuilder.addCommands(SlashCommands.commandMap.values().toArray(SlashCommand[]::new));
        SlashCommandClient slashCommandClient = slashCommandClientBuilder.build();
        builder.addEventListeners(new SlashCommandHandler(), slashCommandClient);

        Main.manager = builder.build();
        Main.manager.awaitReady();

        DBVariables.initiateCurrentServers(Main.manager);
    }

    //
    // Logger
    //

    public static void log(CommandEvent event, String command) {
        System.out.println("[" + event.getGuild().getName() + "]:[" + event.getMember().getEffectiveName() + "]: " + Main.ANSI_YELLOW + "Triggered " + command + Main.ANSI_RESET + ": " + event.getMessage().getContentRaw());
    }

    public static void log(SlashCommandEvent event, String command) {
        System.out.println("[" + event.getGuild().getName() + "]:[" + event.getMember().getEffectiveName() + "]: " + Main.ANSI_YELLOW + "Triggered " + command + Main.ANSI_RESET + ": " + event.getCommandString());
    }

    public static void info(CommandEvent event, String command) {
        System.out.println("[" + event.getGuild().getName() + "]:[" + event.getMember().getEffectiveName() + "]: " + Main.ANSI_YELLOW + command + Main.ANSI_RESET);
    }

    public static void info(SlashCommandEvent event, String command) {
        System.out.println("[" + event.getGuild().getName() + "]:[" + event.getMember().getEffectiveName() + "]: " + Main.ANSI_YELLOW + command + Main.ANSI_RESET);
    }

    public static void info(ButtonClickEvent event, String command) {
        System.out.println("[" + event.getGuild().getName() + "]:[" + event.getMember().getEffectiveName() + "]: " + Main.ANSI_YELLOW + command + Main.ANSI_RESET);
    }

    public static void info(Guild guild, String command) {
        System.out.println("[" + guild.getName() + "]: " + Main.ANSI_YELLOW + command + Main.ANSI_RESET);
    }

    public static void info(CommandEvent event, String command, String color) {
        System.out.println("[" + event.getGuild().getName() + "]:[" + event.getMember().getEffectiveName() + "]: " + color + command + Main.ANSI_RESET);
    }

    public static void info(SlashCommandEvent event, String command, String color) {
        System.out.println("[" + event.getGuild().getName() + "]:[" + event.getMember().getEffectiveName() + "]: " + color + command + Main.ANSI_RESET);
    }

    public static void info(Guild guild, String command, String color) {
        System.out.println("[" + guild.getName() + "]: " + color + command + Main.ANSI_RESET);
    }
}
