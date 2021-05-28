package handlers;

import commands.Command;
import commands.essentials.Info;
import commands.essentials.Ping;
import commands.music.*;
import commands.perms.EditPerms;
import commands.perms.ShowPerms;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;

import java.util.HashMap;

public class SlashCommandHandler
{
    public static HashMap<String, Command> commandMap = new HashMap<>();

    public static void initializeSlashCommands()
    {
        commandMap.put("ping", new Ping());
        commandMap.put("info", new Info());

        commandMap.put("player", new Player());

        commandMap.put("join", new Join());
        commandMap.put("disconnect", new Disconnect());
        commandMap.put("play", new Play());
        commandMap.put("pause", new Pause());
        commandMap.put("resume", new Resume());
        commandMap.put("stop", new Stop());
        commandMap.put("skip", new Skip());
        commandMap.put("volume", new Volume());

        commandMap.put("editperms", new EditPerms());
        commandMap.put("showperms", new ShowPerms());
    }

    public static void updateSlashCommands(JDA jda) throws InterruptedException
    {
        initializeSlashCommands();

        CommandUpdateAction commands = jda.getGuildById("842695566709751828").updateCommands();

        for(Command cmd : commandMap.values())
        {
            commands.addCommands(cmd.commandInfo());
        }

        commands.queue();
    }

}
