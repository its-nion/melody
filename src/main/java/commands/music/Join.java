package commands.music;

import audioCore.AudioStateChecks;
import commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;

public class Join implements Command
{
    @Override
    public CommandData commandInfo()
    {
        return new CommandData("join", "Pulls Melody into your voice channel");
    }

    @Override
    public void called(SlashCommandEvent event)
    {
        if(AudioStateChecks.isMemberInVC(event) == false)
        {
            event.replyEmbeds(new EmbedBuilder()
                    .setColor(new Color(248,78,106,255))
                    .setDescription("This command requires you to be **connected to a voice channel**")
                    .build())
                    .queue();

            return;
        }

        this.action(event);
    }

    @Override
    public void action(SlashCommandEvent event)
    {
        AudioManager audioManager = event.getGuild().getAudioManager();
        VoiceChannel memberChannel = event.getMember().getVoiceState().getChannel();

        if(AudioStateChecks.isMelodyInVC(event))
        {
            audioManager.openAudioConnection(memberChannel);

            event.replyEmbeds(new EmbedBuilder()
                    .setColor(new Color(88,199,235,255))
                    .setDescription("**Moved** to <#" + memberChannel.getId() + ">")
                    .build())
                    .queue();
            return;
        }
        else
        {
            audioManager.openAudioConnection(memberChannel);

            event.replyEmbeds(new EmbedBuilder()
                    .setColor(new Color(116,196,118,255))
                    .setDescription("**Joined** in <#" + memberChannel.getId() + ">")
                    .build())
                    .queue();

            return;
        }
    }
}
