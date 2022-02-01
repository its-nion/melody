package com.lopl.melody.audio.provider;

import com.lopl.melody.audio.provider.spotify.Spotify;
import com.lopl.melody.settings.SettingsManager;
import com.lopl.melody.settings.items.DefaultMusicType;
import com.lopl.melody.utils.Logging;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;

public interface MusicDataSearcher<T, P, A, U> {

  private static boolean checkType(String check, String... keywords) {
    for (String key : keywords) {
      if (check.equals(key)) return true;
    }
    return false;
  }

  private static String removeAll(String word, String... ignores) {
    for (String ignored : ignores)
      word = word.replaceAll(ignored, "");
    return word.strip();
  }

  default void search(@NotNull SlashCommandEvent event, @NotNull String search, Message message) {
    String[] args = search.split(" ");
    if (args.length == 0 || args[0] == null || args[0].isEmpty()) return;

    if (checkType(args[0], "playlist", "pl", "list", "playlists")) {
      Logging.debug(Spotify.class, event.getGuild(), null, "Searching on " + getClass().getSimpleName() + " for Playlists with: " + removeAll(search, "playlist", "pl", "list", "playlists", "ytsearch:"));
      P[] playlists = searchPlaylists(removeAll(search, "playlist", "pl", "list", "playlists"));
      onPlaylistSearch(playlists, message);
    } else if (checkType(args[0], "tracks", "track", "song")) {
      Logging.debug(Spotify.class, event.getGuild(), null, "Searching on " + getClass().getSimpleName() + " for Tracks with: " + removeAll(search, "tracks", "track", "song", "ytsearch:"));
      T[] tracks = searchTracks(removeAll(search, "tracks", "track", "song"));
      onTrackSearch(tracks, message);
    } else if (checkType(args[0], "user", "artist", "interpret", "account")) {
      Logging.debug(Spotify.class, event.getGuild(), null, "Searching on " + getClass().getSimpleName() + " for Users with: " + removeAll(search, "user", "artist", "interpret", "account", "ytsearch:"));
      A[] playlists = searchUser(removeAll(search, "user", "artist", "interpret", "account"));
      U userName = searchUserName(removeAll(search, "user", "artist", "interpret", "account"));
      onUserSearch(playlists, userName, message);
    } else {
      if (event.getGuild() == null) return;
      DefaultMusicType defaultMusicType = SettingsManager.getInstance().getGuildSettings(event.getGuild()).getSetting(DefaultMusicType.class);
      switch (defaultMusicType.getValue().getData()) {
        case DefaultMusicType.Value.PLAYLIST -> search(event, "playlist " + search, message);
        case DefaultMusicType.Value.TRACK -> search(event, "track " + search, message);
        case DefaultMusicType.Value.USER -> search(event, "user " + search, message);
      }
    }
  }

  T[] searchTracks(@NotNull String search);

  P[] searchPlaylists(@NotNull String search);

  A[] searchUser(@NotNull String search);

  U searchUserName(@NotNull String search);

  void onUserSearch(A[] playlists, U userName, Message message);

  void onPlaylistSearch(P[] playlists, Message message);

  void onTrackSearch(T[] tracks, Message message);

}
