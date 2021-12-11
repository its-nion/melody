package commands.music;

import audioCore.handler.AudioStateChecks;
import audioCore.slash.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;

public class Join extends SlashCommand {


    @Override
    protected void execute(SlashCommandEvent event) {
        if(!AudioStateChecks.isMemberInVC(event))
        {
            event.replyEmbeds(new EmbedBuilder()
                .setColor(new Color(248,78,106,255))
                .setDescription("This command requires you to be **connected to a voice channel**")
                .build())
                .queue();

            return;
        }

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
        }
        else
        {
            audioManager.openAudioConnection(memberChannel);

            event.replyEmbeds(new EmbedBuilder()
                .setColor(new Color(116,196,118,255))
                .setDescription("**Joined** in <#" + memberChannel.getId() + ">")
                .build())
                .queue();

        }
    }

    @Override
    protected void clicked(ButtonClickEvent event) {

    }
}
