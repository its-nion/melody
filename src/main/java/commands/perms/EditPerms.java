package commands.perms;

import commands.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import static net.dv8tion.jda.api.interactions.commands.OptionType.ROLE;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class EditPerms implements Command
{

    @Override
    public CommandData commandInfo() {
        return new CommandData("editperms", "Edit role permissions")
                .addOption(new OptionData(ROLE, "role", "The role you want to edit permissions on")
                        .setRequired(true))

                .addOption(new OptionData(STRING, "action", "What you want to do with the permission")
                        .setRequired(true)
                .addChoice("allow", "allow")
                .addChoice("deny", "deny")
                .addChoice("clear", "clear"))

                .addOption(new OptionData(STRING, "permission", "Which permission you want to edit")
                .setRequired(true)
                .addChoice("play", "play"));
    }

    @Override
    public void called(SlashCommandEvent event) {

    }

    @Override
    public void action(SlashCommandEvent event) {

    }
}
