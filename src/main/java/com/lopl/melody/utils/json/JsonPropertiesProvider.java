package com.lopl.melody.utils.json;

import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

import java.awt.*;
import java.io.IOException;

public class JsonPropertiesProvider {

  private final PropertiesData data;

  public JsonPropertiesProvider(PropertiesData data) {
    this.data = data;
  }

  public String getBotKey(){
    return data.botKey;
  }

  public Color getErrorColor(){
    return new Color(data.colorError[0], data.colorError[1], data.colorError[2], data.colorError[3]);
  }

  public Color getJoinColor(){
    return new Color(data.colorJoin[0], data.colorJoin[1], data.colorJoin[2], data.colorJoin[3]);
  }

  public Color getMoveColor(){
    return new Color(data.colorMove[0], data.colorMove[1], data.colorMove[2], data.colorMove[3]);
  }

  public SpotifyApi getSpotifyApi(){
    SpotifyApi spotifyApi = new SpotifyApi.Builder()
        .setClientId(data.spotifyClientID)
        .setClientSecret(data.spotifyClientSecret)
        .setRefreshToken(data.spotifyClientRefreshToken)
        .build();
    AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh().build();
    boolean result = authorizationCodeRefresh_Sync(authorizationCodeRefreshRequest);
    if (!result) return null;
    else return spotifyApi;
  }

  private static boolean authorizationCodeRefresh_Sync(AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest) {
    try {
      authorizationCodeRefreshRequest.execute();
      return true;
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      return false;
    }
  }
}
