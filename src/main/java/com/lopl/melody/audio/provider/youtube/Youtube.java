package com.lopl.melody.audio.provider.youtube;

import com.lopl.melody.audio.provider.MusicDataSearcher;
import com.lopl.melody.audio.provider.spotify.Spotify;
import com.lopl.melody.settings.SettingsManager;
import com.lopl.melody.settings.items.DefaultMusicType;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.message.MessageStore;
import com.sedmelluq.discord.lavaplayer.source.youtube.*;
import com.sedmelluq.discord.lavaplayer.track.*;
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

    YoutubeMessage youtubeMessage;
    if (checkType(args[0], "playlist", "pl", "list", "playlists")) {
      Logging.debug(Spotify.class, event.getGuild(), null, "Searching on Youtube for Playlists with: " + removeAll(search, "playlist", "pl", "list", "playlists", "ytsearch:"));
      AudioPlaylist[] playlists = getPlaylists(search, "playlist", "pl", "list", "playlists");
      youtubeMessage = new YoutubeMessage(message, playlists);
//    } else if (args[0].equals("user") || args[0].equals("account")) {
    } else if (checkType(args[0], "tracks", "track", "song")) {
      Logging.debug(Spotify.class, event.getGuild(), null, "Searching on Youtube for Tracks with: " + removeAll(search, "tracks", "track", "song", "ytsearch:"));
      AudioTrack[] tracks = getTracks(search, "tracks", "track", "song");
      youtubeMessage = new YoutubeMessage(message, tracks);
    } else {
      if (event.getGuild() == null) return;
      DefaultMusicType defaultMusicType = SettingsManager.getInstance().getGuildSettings(event.getGuild()).getSetting(DefaultMusicType.class);
      switch (defaultMusicType.getValue().getData()){
        case DefaultMusicType.Value.PLAYLIST -> searchYoutube(event, "playlist " + search, message);
        case DefaultMusicType.Value.TRACK -> searchYoutube(event, "track " + search, message);
//        case DefaultMusicType.Value.USER: searchYoutube(event, "user " + search, message);
      }
      return;
    }

    MessageStore.saveMessage(youtubeMessage);
    youtubeMessage.show();
  }

  private static AudioTrack[] getTracks(String name, String... ignores) {
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

  public static AudioPlaylist[] getPlaylists(String name, String... ignores){
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
