package com.lopl.melody.audio.handler;

import com.lopl.melody.utils.Logging;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MusicLoader {

  public void loadOne(TextChannel channel, String trackUrl) {
    PlayerManager player = PlayerManager.getInstance();

    GuildAudioManager musicManager = player.getGuildAudioManager(channel.getGuild());

    player.playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        Logging.debug(getClass(), channel.getGuild(), null, "Loading of song: " + track.getInfo().title + " complete");
        channel.sendMessageEmbeds(getMessage(track, musicManager)).queue();
        // Play the song
        player.play(musicManager, track);
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
        // Only load the first One
        trackLoaded(playlist.getTracks().get(0));
      }

      @Override
      public void noMatches() {
        Logging.debug(getClass(), channel.getGuild(), null, "Loading of song: " + trackUrl + " failed");
      }

      @Override
      public void loadFailed(FriendlyException exception) {
        exception.printStackTrace();
      }

      private MessageEmbed getMessage(AudioTrack track, GuildAudioManager musicManager) {
        AudioTrack playing = musicManager.player.getPlayingTrack();
        ArrayList<AudioTrack> queue = musicManager.scheduler.getQueue();
        return new EmbedBuilder()
            .setAuthor("Track added to queue!")
            .setTitle(track.getInfo().title)
            .setDescription(
                "Duration: " + new SimpleDateFormat("mm:ss").format(new Date(track.getDuration())) + " \n" +
                    "Position in queue: " + (playing == null ? "Now playing" : (queue.size() + 1) + " \n" +
                    "Estimated time until playing: " + new SimpleDateFormat("mm:ss").format(
                    new Date(queue.parallelStream().mapToLong(AudioTrack::getDuration).sum() + playing.getDuration() - playing.getPosition()))))
            .build();
      }
    });
  }

  public void loadMultiple(TextChannel channel, PlaylistSimplified playlist, String... trackUrls) {
    // capture data
    PlayerManager player = PlayerManager.getInstance();
    GuildAudioManager musicManager = player.getGuildAudioManager(channel.getGuild());
    AudioTrack playing = musicManager.player.getPlayingTrack();
    ArrayList<AudioTrack> queue = musicManager.scheduler.getQueue();

    // calculate values
    String positionInQueue = (playing == null ? "Now playing" : "" + (queue.size() + 1));
    String timeUntilPlaying = new SimpleDateFormat("mm:ss").format(
        new Date(queue.parallelStream().mapToLong(AudioTrack::getDuration).sum() + (playing == null ? 0 : playing.getDuration() - playing.getPosition())));

    // build embed
    EmbedBuilder embedBuilder = new EmbedBuilder()
        .setAuthor("Playlist added to queue!")
        .setTitle(playlist.getName())
        .setDescription(
            "0 / " + trackUrls.length + " tracks loaded\n" +
                "Position in queue: " + positionInQueue + " \n" +
                "Estimated time until playing: " + timeUntilPlaying);
    Message message = channel.sendMessageEmbeds(embedBuilder.build()).complete();

    // load tracks
    final int[] iLoaded = {0};
    for (String trackUrl : trackUrls)
      player.playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
        @Override
        public void trackLoaded(AudioTrack track) {
          Logging.debug(getClass(), channel.getGuild(), null, "Loading of song: " + track.getInfo().title + " in playlist " + (iLoaded[0] + 1) + "/" + trackUrls.length);
          player.play(musicManager, track);
          iLoaded[0] += 1;

          // update embed
          if (message != null) {
            embedBuilder.setDescription(
                iLoaded[0] + " / " + trackUrls.length + " tracks \n" +
                    "Position in queue: " + positionInQueue + " \n" +
                    "Estimated time until playing: " + timeUntilPlaying);
            message.editMessageEmbeds(embedBuilder.build()).queue();
          }
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
          // Only load the first One
          trackLoaded(playlist.getTracks().get(0));
        }

        @Override
        public void noMatches() {
          Logging.debug(getClass(), channel.getGuild(), null, "Loading of song: " + trackUrl + " failed");
        }

        @Override
        public void loadFailed(FriendlyException exception) {
          exception.printStackTrace();
        }
      });

  }

  public void loadURL(TextChannel channel, String trackUrl) {
    // collect data
    PlayerManager player = PlayerManager.getInstance();
    GuildAudioManager musicManager = player.getGuildAudioManager(channel.getGuild());

    // load track(s)
    player.playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        Logging.debug(getClass(), channel.getGuild(), null, "Loading of song: " + track.getInfo().title + " complete");
        channel.sendMessageEmbeds(getMessage(track, musicManager)).queue();
        player.play(musicManager, track);
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
        channel.sendMessageEmbeds(getMessage(playlist, musicManager)).queue();
        for (AudioTrack track : playlist.getTracks())
          player.play(musicManager, track);
      }

      @Override
      public void noMatches() {
        Logging.debug(getClass(), channel.getGuild(), null, "Loading of song: " + trackUrl + " failed");
        channel.sendMessage("Nothing found by " + trackUrl).queue();
      }

      @Override
      public void loadFailed(FriendlyException exception) {
        channel.sendMessage("Could not play " + trackUrl).queue();
      }

      private MessageEmbed getMessage(AudioTrack track, GuildAudioManager musicManager) {
        AudioTrack playing = musicManager.player.getPlayingTrack();
        ArrayList<AudioTrack> queue = musicManager.scheduler.getQueue();
        return new EmbedBuilder()
            .setAuthor("Track added to queue!")
            .setTitle(track.getInfo().title)
            .setDescription(
                "Duration: " + new SimpleDateFormat("mm:ss").format(new Date(track.getDuration())) + " \n" +
                    "Position in queue: " + (playing == null ? "Now playing" : (queue.size() + 1) + " \n" +
                    "Estimated time until playing: " + new SimpleDateFormat("mm:ss").format(
                    new Date(queue.parallelStream().mapToLong(AudioTrack::getDuration).sum() + playing.getDuration() - playing.getPosition()))))
            .build();
      }

      private MessageEmbed getMessage(AudioPlaylist playlist, GuildAudioManager musicManager) {
        AudioTrack playing = musicManager.player.getPlayingTrack();
        ArrayList<AudioTrack> queue = musicManager.scheduler.getQueue();
        return new EmbedBuilder()
            .setAuthor("Playlist added to queue!")
            .setTitle(playlist.getName())
            .setDescription(
                "" + playlist.getTracks().size() + " tracks \n" +
                    "Duration: " + new SimpleDateFormat("mm:ss").format(new Date(playlist.getTracks().parallelStream().mapToLong(AudioTrack::getDuration).sum())) + " \n" +
                    "Position in queue: " + (playing == null ? "Now playing" : (queue.size() + 1) + " \n" +
                    "Estimated time until playing: " + new SimpleDateFormat("mm:ss").format(
                    new Date(queue.parallelStream().mapToLong(AudioTrack::getDuration).sum() + playing.getDuration() - playing.getPosition()))))
            .build();
      }
    });

  }

}
