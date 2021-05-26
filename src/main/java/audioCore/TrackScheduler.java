package audioCore;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter
{
    public final AudioPlayer player;
    public final BlockingQueue<AudioTrack> queue;


    public TrackScheduler(AudioPlayer player)
    {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track)
    {
        if(!this.player.startTrack(track, true))
        {
            this.queue.offer(track);
        }
    }

    public void nextTrack()
    {
        this.player.startTrack(this.queue.poll(), false);
    }

    public void skipTracks(SlashCommandEvent event, long amount)
    {
        if(amount > 0 && this.queue.size() >= amount)
        {
            for (long i = 0L; i < amount; i++)
            {
                this.player.startTrack(this.queue.poll(), false);
            }

            event.reply(new EmbedBuilder()
                    .setDescription("**Skipped " + event.getOption("amount").getAsLong() + "** songs")
                    .build())
                    .queue();
        }
        else
        {
            event.reply(new EmbedBuilder()
                    .setColor(new Color(248,78,106,255))
                    .setDescription("Cant skip **" + event.getOption("amount").getAsLong() + "** tracks")
                    .build())
                    .queue();
        }
    }


    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(endReason.mayStartNext)
        {
            nextTrack();
        }
    }
}
