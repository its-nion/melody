package commands.music;

import audioCore.handler.AudioStateChecks;
import audioCore.handler.PlayerManager;
import audioCore.handler.TrackScheduler;
import audioCore.slash.SlashCommand;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.awt.*;

public class Skip extends SlashCommand {

//    @Override
//    public CommandData commandInfo() {
//        return new CommandData("skip", "Skips the current track")
//                .addOption(new OptionData(INTEGER, "amount", "How many songs to skip")
//                        .setRequired(false));
//    }


    @Override
    protected void execute(SlashCommandEvent event) {
        if(!AudioStateChecks.isMemberInVC(event))
        {
            event.replyEmbeds(new EmbedBuilder()
                .setColor(new Color(248,78,106,255))
                .setDescription("This Command requires **you** to be **connected to a voice channel**")
                .build())
                .queue();

            return;
        }

        if(!AudioStateChecks.isMelodyInVC(event))
        {
            event.replyEmbeds(new EmbedBuilder()
                .setColor(new Color(248,78,106,255))
                .setDescription("This Command requires Melody to be **connected to your voice channel**")
                .build())
                .queue();

            return;
        }

        if(!AudioStateChecks.isMemberAndMelodyInSameVC(event))
        {
            event.replyEmbeds(new EmbedBuilder()
                .setColor(new Color(248,78,106,255))
                .setDescription("This Command requires Melody to be **connected to your voice channel**")
                .build())
                .queue();

            return;
        }

        final AudioPlayer audioPlayer = PlayerManager.getInstance().getGuildAudioManager(event.getGuild()).player;

        if(audioPlayer.getPlayingTrack() == null)
        {
            event.replyEmbeds(new EmbedBuilder()
                .setColor(new Color(248,78,106,255))
                .setDescription("There is currently **no track playing**")
                .build())
                .queue();

            return;
        }

        final OptionMapping option = event.getOption("amount");

        if(option == null)
        {
            PlayerManager.getInstance().getGuildAudioManager(event.getGuild()).scheduler.nextTrack();

            event.replyEmbeds(new EmbedBuilder()
                .setDescription("**Skipped** to the next song")
                .build())
                .queue();
        }
        else
        {
            //PlayerManager.getInstance().getGuildAudioManager(event.getGuild()).scheduler.skipTracks(event, event.getOption("amount").getAsLong());
        }
    }

    @Override
    protected void clicked(ButtonClickEvent event) {

    }

    public boolean skip(Guild guild){
        PlayerManager manager = PlayerManager.getInstance();
        TrackScheduler scheduler = manager.getGuildAudioManager(guild).scheduler;
        return scheduler.nextTrack();
    }

    public void skip(Guild guild, int amount){
        while (amount > 0) {
            boolean skipable = skip(guild);
            if (!skipable) {
                return;
            }
            amount--;
        }
    }
}
