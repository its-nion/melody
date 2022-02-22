package com.lopl.melody.audio.provider.spotify;

import com.google.gson.JsonArray;
import com.lopl.melody.audio.provider.MusicDataSearcher;
import com.lopl.melody.utils.json.JsonProperties;
import com.lopl.melody.utils.message.MessageStore;
import net.dv8tion.jda.api.entities.Message;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.NotNull;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.lopl.melody.Token.*;

public class Spotify implements MusicDataSearcher<Track, PlaylistSimplified, AlbumSimplified, String> {

  public static final SpotifyApi spotifyApi = JsonProperties.getProperties().getSpotifyApi();
  private static final AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh().build(); //TODO: cancel everything if spotify not available

  public static Track[] getPlaylistsTracks(String simpleTrackID) {
    try {
      PlaylistTrack[] tracks = spotifyApi.getPlaylistsItems(simpleTrackID).build().execute().getItems(); //TODO: TooManyRequestsException
      List<Track> ret = new ArrayList<>();
      for (PlaylistTrack pt : tracks) {
        ret.add(spotifyApi.getTrack(pt.getTrack().getId()).build().execute()); //TODO: TooManyRequestsException
      }
      return ret.toArray(Track[]::new);
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Track[] getAlbumTracks(String simpleTrackID) {
    try {
      TrackSimplified[] tracks = spotifyApi.getAlbumsTracks(simpleTrackID).build().execute().getItems();
      List<Track> ret = new ArrayList<>();
      for (TrackSimplified pt : tracks) {
        ret.add(spotifyApi.getTrack(pt.getId()).build().execute());
      }
      return ret.toArray(Track[]::new);
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void authorizationCodeRefresh_Sync() {
    try {
      final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

      // Set access and refresh token for further "spotifyApi" object usage
      spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
      spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

    } catch (IOException | SpotifyWebApiException | ParseException e) {
      e.printStackTrace();
    }
  }

  @Override
  public PlaylistSimplified[] searchPlaylists(@NotNull String name) {
    try {
      // refresh Spotify
      authorizationCodeRefresh_Sync();

      return spotifyApi.searchItem(name, ModelObjectType.PLAYLIST.getType())
          .build().execute()
          .getPlaylists().getItems();
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Track[] searchTracks(@NotNull String name) {
    try {
      // refresh Spotify
      authorizationCodeRefresh_Sync();

      return spotifyApi.searchItem(name, ModelObjectType.TRACK.getType())
          .build().execute()
          .getTracks().getItems();
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public AlbumSimplified[] searchUser(@NotNull String search) {
    try {
      // refresh Spotify
      authorizationCodeRefresh_Sync();

      Paging<Artist> artistResult = spotifyApi.searchArtists(search).build().execute();
      Artist artist = artistResult.getItems()[0];
      return spotifyApi.getArtistsAlbums(artist.getId()).build().execute().getItems();
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public String searchUserName(@NotNull String search) {
    try {
      // refresh Spotify
      authorizationCodeRefresh_Sync();

      Paging<Artist> artistResult = spotifyApi.searchArtists(search).build().execute();
      return artistResult.getItems()[0].getName();
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void onUserSearch(AlbumSimplified[] playlists, String userName, Message message) {
    SpotifyMessage spotifyMessage = new SpotifyMessage(message, playlists);
    MessageStore.saveMessage(spotifyMessage);
    spotifyMessage.show();
  }

  @Override
  public void onPlaylistSearch(PlaylistSimplified[] playlists, Message message) {
    SpotifyMessage spotifyMessage = new SpotifyMessage(message, playlists);
    MessageStore.saveMessage(spotifyMessage);
    spotifyMessage.show();
  }

  @Override
  public void onTrackSearch(Track[] tracks, Message message) {
    SpotifyMessage spotifyMessage = new SpotifyMessage(message, tracks);
    MessageStore.saveMessage(spotifyMessage);
    spotifyMessage.show();
  }

}


