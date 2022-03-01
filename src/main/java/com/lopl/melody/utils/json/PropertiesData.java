package com.lopl.melody.utils.json;

/**
 * This is the data holder for the properties.json.
 */
public class PropertiesData {

  // Do not use primitive types!
  public String botKey;
  public String secretPropertiesLocation; // this is used for simple open source versioning.
  public Float[] colorError;
  public Float[] colorJoin;
  public Float[] colorMove;
  public String spotifyClientID;
  public String spotifyClientSecret;
  public String spotifyClientRefreshToken;


  /**
   * This will automatically update all the values
   * @return a updated version
   */
  public static PropertiesData generateNew() {
    PropertiesData data = new PropertiesData();
    return generateNew(data);
  }

  /**
   * This will automatically update all the values
   * @param preExisting a instance that is merged
   * @return a updated version
   */
  public static PropertiesData generateNew(PropertiesData preExisting) {
    if (preExisting.botKey == null) preExisting.botKey = "";
    if (preExisting.secretPropertiesLocation == null) preExisting.secretPropertiesLocation = "";
    if (preExisting.colorError == null) preExisting.colorError = new Float[]{248f, 78f, 106f, 255f};
    if (preExisting.colorJoin == null) preExisting.colorJoin = new Float[]{116f, 196f, 118f, 255f};
    if (preExisting.colorMove == null) preExisting.colorMove = new Float[]{88f, 199f, 235f, 255f};
    if (preExisting.spotifyClientID == null) preExisting.spotifyClientID = "";
    if (preExisting.spotifyClientSecret == null) preExisting.spotifyClientSecret = "";
    if (preExisting.spotifyClientRefreshToken == null) preExisting.spotifyClientRefreshToken = "";
    return preExisting;
  }

  /**
   * This will include data from a secret location to the object.
   * @param preExisting a instance that is merged
   * @param secret a secret not versioned instance
   * @return a complete version
   */
  public static PropertiesData addSecret(PropertiesData preExisting, PropertiesData secret) {
    if (!secret.botKey.isEmpty()) preExisting.botKey = secret.botKey;
    if (!secret.spotifyClientID.isEmpty()) preExisting.spotifyClientID = secret.spotifyClientID;
    if (!secret.spotifyClientSecret.isEmpty()) preExisting.spotifyClientSecret = secret.spotifyClientSecret;
    if (!secret.spotifyClientRefreshToken.isEmpty()) preExisting.spotifyClientRefreshToken = secret.spotifyClientRefreshToken;
    return preExisting;
  }

}
