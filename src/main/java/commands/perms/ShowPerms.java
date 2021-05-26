package commands.perms;

import commands.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import static net.dv8tion.jda.api.interactions.commands.OptionType.ROLE;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class ShowPerms implements Command
{

    @Override
    public CommandData commandInfo() {
        return new CommandData("showperms", "Shows role permissions")
                .addOption(new OptionData(ROLE, "role", "The role which permissions you want to see")
                        .setRequired(true));
    }

    @Override
    public void called(SlashCommandEvent event) {

    }

    @Override
    public void action(SlashCommandEvent event) {

    }
}
