package com.lopl.melody.audio.provider.youtube;

import com.lopl.melody.audio.provider.MusicDataSearcher;
import com.lopl.melody.audio.provider.spotify.Spotify;
import com.lopl.melody.audio.provider.spotify.SpotifyMessage;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.message.MessageStore;
import com.sedmelluq.discord.lavaplayer.source.youtube.*;
import com.sedmelluq.discord.lavaplayer.track.*;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Youtube implements MusicDataSearcher {

  public static void searchYoutube(@NotNull SlashCommandEvent event, @NotNull String search, Message message) {
    Logging.debug(Youtube.class, event.getGuild(), null, "Youtube Re-Loaded");

    String[] args = search.split(" ");
    if (args.length == 0 || args[0] == null || args[0].isEmpty()) return;

    YoutubeMessage youtubeMessage = null;
    if (checkType(args[0], "playlist", "pl", "list")) {
      Logging.debug(Spotify.class, event.getGuild(), null, "Searching on Youtube for Playlists with: " + removeAll(search, "tracks", "track", "song", "ytsearch:"));
      AudioPlaylist[] playlists = getPlaylists(search, new String[]{"playlist", "pl", "list"});
      youtubeMessage = new YoutubeMessage(message, playlists);
    } else if (args[0].equals("user") || args[0].equals("account")) {
//      // USER
//      SpotifyMessage spotifyMessage =
//          new SpotifyMessage(message, getUserPlaylists(search.replaceAll("user ", "").replaceAll("account ", "")));
    } else if (checkType(args[0], "tracks", "track", "song")) {
      Logging.debug(Spotify.class, event.getGuild(), null, "Searching on Youtube for Tracks with: " + removeAll(search, "tracks", "track", "song", "ytsearch:"));
      AudioTrack[] tracks = getTracks(search, new String[]{"tracks", "track", "song"});
      youtubeMessage = new YoutubeMessage(message, tracks);
    } else {
      Logging.debug(Spotify.class, event.getGuild(), null, "Searching on Youtube for Tracks with: " + removeAll(search, "ytsearch:"));
      AudioTrack[] tracks = getTracks(search, new String[0]);
      youtubeMessage = new YoutubeMessage(message, tracks);
    }

    MessageStore.saveMessage(youtubeMessage);
    if (youtubeMessage != null)
      youtubeMessage.show();
  }

  private static AudioTrack[] getTracks(String name, String[] ignores) {
    for (String ignored : ignores)
      name = name.replaceAll(ignored, "");

    YoutubeAudioSourceManager yasm = new YoutubeAudioSourceManager(true);
    AudioItem result = yasm.loadItem(null, new AudioReference("ytsearch:" + name, null));
    if (result instanceof BasicAudioPlaylist) {
      BasicAudioPlaylist resultPlaylist = (BasicAudioPlaylist) result;
      return resultPlaylist.getTracks().toArray(AudioTrack[]::new);
    }
    return null;
  }

  public static AudioPlaylist[] getPlaylists(String name, String[] ignores){
    for (String ignored : ignores)
      name = name.replaceAll(ignored, "");

    YoutubeAudioSourceManager yasm = new YoutubeAudioSourceManager(true, new DefaultYoutubeTrackDetailsLoader(), new YoutubePlaylistSearchProvider(), new YoutubeSearchMusicProvider(), new YoutubeSignatureCipherManager(), new DefaultYoutubePlaylistLoader(), new DefaultYoutubeLinkRouter(), new YoutubeMixProvider());
    AudioItem result = yasm.loadItem(null, new AudioReference("ytsearch:" + name, null));
    if (result instanceof AudioPlaylist) {
      BasicAudioPlaylist resultPlaylist = (BasicAudioPlaylist) result;
      List<AudioTrack> pls = resultPlaylist.getTracks();
      List<AudioPlaylist> playlists = new ArrayList<>();
      for (AudioTrack audioTrack : pls){
        AudioItem pl = yasm.loadItem(null, new AudioReference(audioTrack.getInfo().uri, null));
        if (pl instanceof AudioPlaylist){
          AudioPlaylist apl = (AudioPlaylist) pl;
          playlists.add(apl);
        }
      }
      return playlists.toArray(AudioPlaylist[]::new);
    }
    return null;
  }

  private static String removeAll(String word, String... ignores) {
    for (String ignored : ignores)
      word = word.replaceAll(ignored, "");
    return word.strip();
  }

  private static boolean checkType(String check, String... keywords) {
    for (String key : keywords) {
      if (check.equals(key)) return true;
    }
    return false;
  }

  @Override
  public void search(@NotNull SlashCommandEvent event, @NotNull String search, Message message) {
    searchYoutube(event, search, message);
  }

}
