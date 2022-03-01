package com.lopl.melody.utils.json;

import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

import java.awt.*;
import java.io.IOException;

/**
 * This class provides some useful methods to access the data of the properties.json.
 */
public class JsonPropertiesProvider {

  private final PropertiesData data;

  public JsonPropertiesProvider(PropertiesData data) {
    this.data = data;
  }

  /**
   * @return the discord bot key for this melody
   */
  public String getBotKey(){
    return data.botKey;
  }

  /**
   * @return a color instance that is stored in the json as a float array.
   */
  public Color getErrorColor(){
    return Color.decode(data.colorError);
  }

  /**
   * @return a color instance that is stored in the json as a float array.
   */
  public Color getJoinColor(){
    return Color.decode(data.colorJoin);
  }

  /**
   * @return a color instance that is stored in the json as a float array.
   */
  public Color getMoveColor(){
    return Color.decode(data.colorMove);
  }

  /**
   * @return a fully built instance of SpotifyApi if the api key values are correct.
   * else null
   */
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
