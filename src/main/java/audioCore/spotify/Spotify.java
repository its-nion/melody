package audioCore.spotify;

import audioCore.slash.MessageStore;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import utils.Logging;

import java.io.IOException;

import static melody.Token.*;


public class Spotify {

  public static void searchSpotify(@NotNull SlashCommandEvent event, @NotNull String search, Message message) {

    // refresh Spotify
    authorizationCodeRefresh_Sync();
    Logging.info(Spotify.class, event.getGuild(), event.getMember(), "Spotify Loaded");
    //melody.Main.info(event, "Starting looking", melody.Main.ANSI_GREEN);

    // determine type
    String[] args = search.split(" ");
    if (args.length == 0 || args[0] == null || args[0].isEmpty()) return;
    if (checkType(args[0], "playlist", "pl", "list")) {
      // PLAYLIST
      // api request
      Logging.debug(Spotify.class, event.getGuild(), event.getMember(), "Searching on Spotify for Tracks with: " + removeAll(search, "tracks", "track", "song", "ytsearch:"));
      PlaylistSimplified[] playlists = getPlaylists(search, new String[]{"playlist", "pl", "list"});
      // save data
      SpotifyButtonMessage spotifyMessage = new SpotifyButtonMessage(message, playlists);
      MessageStore.saveMessage(spotifyMessage);
      // show data
      spotifyMessage.show();
    } else if (args[0].equals("user") || args[0].equals("account")) {
//      // USER
//      SpotifyMessage spotifyMessage =
//          new SpotifyMessage(message, getUserPlaylists(search.replaceAll("user ", "").replaceAll("account ", "")));
//      melody.Main.info(event, "Added Message", melody.Main.ANSI_GREEN);
//      MessageStore.saveMessage(spotifyMessage);
//      spotifyMessage.show();
    } else if (checkType(args[0], "tracks", "track", "song")) {
      // TRACK
      // api request
      Logging.debug(Spotify.class, event.getGuild(), event.getMember(), "Searching on Spotify for Tracks with: " + removeAll(search, "tracks", "track", "song", "ytsearch:"));
      Track[] tracks = getTracks(search, new String[]{"tracks", "track", "song"});
      // save data
      SpotifyMessage spotifyMessage = new SpotifyMessage(message, tracks);
      MessageStore.saveMessage(spotifyMessage);
      // show data
      spotifyMessage.show();
    } else {
      // UNKNOWN -> apply default: TRACK
      // api request
      Logging.info(Spotify.class, event.getGuild(), event.getMember(), "Searching on Spotify for Tracks with: " + removeAll(search, "ytsearch:"));
      Track[] tracks = getTracks(search, new String[0]);
      // save data
      SpotifyButtonMessage spotifyMessage = new SpotifyButtonMessage(message, tracks);
      MessageStore.saveMessage(spotifyMessage);
      // show data
      spotifyMessage.show();
    }
  }

  private static PlaylistSimplified[] getPlaylists(String name, String[] ignores) {
    for (String ignored : ignores)
      name = name.replaceAll(ignored, "");
    try {
      return spotifyApi.searchItem(name, ModelObjectType.PLAYLIST.getType())
          .build().execute()
          .getPlaylists().getItems();

    } catch (IOException | SpotifyWebApiException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static Track[] getTracks(String name, String[] ignores) {
    name = removeAll(name, ignores);
    try {
      return spotifyApi.searchItem(name, ModelObjectType.TRACK.getType())
          .build().execute()
          .getTracks().getItems();
    } catch (IOException | SpotifyWebApiException e) {
      e.printStackTrace();
    }
    return null;
  }


  private static PlaylistSimplified[] getUserPlaylists(String id) {
    try {
      return spotifyApi.getListOfUsersPlaylists(id).build().execute().getItems();

    } catch (IOException | SpotifyWebApiException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static PlaylistTrack[] getPlaylistsTracks(String simpleTrackID) {
    try {
      return spotifyApi.getPlaylistsTracks(simpleTrackID)
          .build().execute()
          .getItems();
    } catch (IOException | SpotifyWebApiException e) {
      e.printStackTrace();
    }
    return null;
  }

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


  public static final SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(clientId).setClientSecret(clientSecret).setRefreshToken(refreshToken).build();
  private static final AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh().build();

  public static void authorizationCodeRefresh_Sync() {
    try {
      final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

      // Set access and refresh token for further "spotifyApi" object usage
      spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
      spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

    } catch (IOException | SpotifyWebApiException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }
}


