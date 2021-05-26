package listeners;

import commands.Command;
import handlers.SlashCommandHandler;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;

public class SlashCommandListener extends ListenerAdapter
{
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        HashMap<String, Command> commandMap = SlashCommandHandler.commandMap;

        if (!(event.getGuild() == null) || (commandMap.containsKey(event.getName()))) // Check if message is on server and in command list
        {
            commandMap.get(event.getName()).called(event);
        }
    }
}
