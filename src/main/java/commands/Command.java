package commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.utils.data.DataType;

import javax.annotation.Nullable;

public interface Command
{
    CommandData commandInfo();

    void called(SlashCommandEvent event);

    void action(SlashCommandEvent event);
}
