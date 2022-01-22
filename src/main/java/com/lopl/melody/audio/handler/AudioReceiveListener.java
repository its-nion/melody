package com.lopl.melody.audio.handler;


import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.Mp3Encoder;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;


public class AudioReceiveListener implements AudioReceiveHandler {
  public static final double STARTING_MB = 0.5;
  public static final int CAP_MB = 16;
  public static final double PCM_MINS = 2;
  private final double volume;
  private final VoiceChannel voiceChannel;
  public boolean canReceive = true;
  public byte[] uncompVoiceData = new byte[(int) (3840 * 50 * 60 * PCM_MINS)]; //3840bytes/array * 50arrays/sec * 60sec = 1 mins
  public int uncompIndex = 0;
  public byte[] compVoiceData = new byte[(int) (1024 * 1024 * STARTING_MB)];    //start with 0.5 MB
  public int compIndex = 0;
  public boolean overwriting = false;

  public byte[] uncompUserVoiceData = new byte[(int) (3840 * 50 * 60 * PCM_MINS)]; //3840bytes/array * 50arrays/sec * 60sec = 1 mins
  public int uncompUserIndex = 0;
  public byte[] compUserVoiceData = new byte[(int) (1024 * 1024 * STARTING_MB)];    //start with 0.5 MB
  public int compUserIndex = 0;
  public boolean overwritingUser = false;

  public AudioReceiveListener(double volume, VoiceChannel voiceChannel) {
    this.volume = volume;
    this.voiceChannel = voiceChannel;
  }

  @Override
  public boolean canReceiveCombined() {
    return canReceive;
  }

  @Override
  public boolean canReceiveUser() {
    return canReceive;
  }

  @Override
  public void handleCombinedAudio(@NotNull CombinedAudio combinedAudio) {
    if (uncompIndex == uncompVoiceData.length / 2 || uncompIndex == uncompVoiceData.length) {
      new Thread(() -> {

        if (uncompIndex < uncompVoiceData.length / 2)  //first half
          addCompVoiceData(Mp3Encoder.encodePcmToMp3(Arrays.copyOfRange(uncompVoiceData, 0, uncompVoiceData.length / 2)));
        else
          addCompVoiceData(Mp3Encoder.encodePcmToMp3(Arrays.copyOfRange(uncompVoiceData, uncompVoiceData.length / 2, uncompVoiceData.length)));
      }).start();

      if (uncompIndex == uncompVoiceData.length)
        uncompIndex = 0;
    }

    for (byte b : combinedAudio.getAudioData(volume)) {
      uncompVoiceData[uncompIndex++] = b;
    }
  }

  @Override
  public void handleUserAudio(@NotNull UserAudio userAudio) {
    if (uncompUserIndex == uncompUserVoiceData.length / 2 || uncompUserIndex == uncompUserVoiceData.length) {
      new Thread(() -> {
        if (uncompUserIndex < uncompUserVoiceData.length / 2)  //first half
          addCompUserVoiceData(Mp3Encoder.encodePcmToMp3(Arrays.copyOfRange(uncompUserVoiceData, 0, uncompUserVoiceData.length / 2)));
        else
          addCompUserVoiceData(Mp3Encoder.encodePcmToMp3(Arrays.copyOfRange(uncompUserVoiceData, uncompUserVoiceData.length / 2, uncompUserVoiceData.length)));
      }).start();

      if (uncompUserIndex == uncompUserVoiceData.length)
        uncompUserIndex = 0;
    }

    for (byte b : userAudio.getAudioData(volume)) {
      uncompUserVoiceData[uncompUserIndex++] = b;
    }
  }

  public byte[] getVoiceData() {
    canReceive = false;

    //flush remaining audio
    byte[] remaining = new byte[uncompIndex];

    int start = uncompIndex < uncompVoiceData.length / 2 ? 0 : uncompVoiceData.length / 2;

    if (uncompIndex - start >= 0) System.arraycopy(uncompVoiceData, start, remaining, 0, uncompIndex - start);

    addCompVoiceData(Mp3Encoder.encodePcmToMp3(remaining));

    byte[] orderedVoiceData;
    if (overwriting) {
      orderedVoiceData = new byte[compVoiceData.length];
    } else {
      orderedVoiceData = new byte[compIndex + 1];
      compIndex = 0;
    }

    for (int i = 0; i < orderedVoiceData.length; i++) {
      if (compIndex + i < orderedVoiceData.length)
        orderedVoiceData[i] = compVoiceData[compIndex + i];
      else
        orderedVoiceData[i] = compVoiceData[compIndex + i - orderedVoiceData.length];
    }

    wipeMemory();
    canReceive = true;

    return orderedVoiceData;
  }

  @Deprecated
  public byte[] getUserVoiceData() {
    canReceive = false;

    //flush remaining audio
    byte[] remaining = new byte[uncompUserIndex];

    int start = uncompUserIndex < uncompUserVoiceData.length / 2 ? 0 : uncompUserVoiceData.length / 2;

    if (uncompUserIndex - start >= 0)
      System.arraycopy(uncompUserVoiceData, start, remaining, 0, uncompUserIndex - start);

    addCompVoiceData(Mp3Encoder.encodePcmToMp3(remaining));

    byte[] orderedVoiceData;
    if (overwritingUser) {
      orderedVoiceData = new byte[compUserVoiceData.length];
    } else {
      orderedVoiceData = new byte[compUserIndex + 1];
      compUserIndex = 0;
    }

    for (int i = 0; i < orderedVoiceData.length; i++) {
      if (compUserIndex + i < orderedVoiceData.length)
        orderedVoiceData[i] = compUserVoiceData[compUserIndex + i];
      else
        orderedVoiceData[i] = compUserVoiceData[compUserIndex + i - orderedVoiceData.length];
    }

    wipeMemory();
    canReceive = true;

    return orderedVoiceData;
  }


  public void addCompVoiceData(byte[] compressed) {
    for (byte b : compressed) {
      if (compIndex >= compVoiceData.length && compVoiceData.length != 1024 * 1024 * CAP_MB) {    //cap at 16MB

        byte[] temp = new byte[compVoiceData.length * 2];
        System.arraycopy(compVoiceData, 0, temp, 0, compVoiceData.length);

        compVoiceData = temp;

      } else if (compIndex >= compVoiceData.length && compVoiceData.length == 1024 * 1024 * CAP_MB) {
        compIndex = 0;

        if (!overwriting) {
          overwriting = true;
          Logging.debug(getClass(), voiceChannel.getGuild(), null, String.format("Hit compressed storage cap in %s on %s", voiceChannel.getName(), voiceChannel.getGuild().getName()));
        }
      }

      compVoiceData[compIndex++] = b;
    }
  }

  public void addCompUserVoiceData(byte[] compressed) {
    for (byte b : compressed) {
      if (compUserIndex >= compUserVoiceData.length && compUserVoiceData.length != 1024 * 1024 * CAP_MB) {    //cap at 16MB

        byte[] temp = new byte[compUserVoiceData.length * 2];
        System.arraycopy(compUserVoiceData, 0, temp, 0, compUserVoiceData.length);

        compUserVoiceData = temp;

      } else if (compUserIndex >= compUserVoiceData.length && compUserVoiceData.length == 1024 * 1024 * CAP_MB) {
        compUserIndex = 0;

        if (!overwritingUser) {
          overwritingUser = true;
          Logging.debug(getClass(), voiceChannel.getGuild(), null, String.format("Hit compressed storage cap in %s on %s", voiceChannel.getName(), voiceChannel.getGuild().getName()));
        }
      }


      compUserVoiceData[compUserIndex++] = b;
    }
  }


  public void wipeMemory() {
    Logging.debug(getClass(), voiceChannel.getGuild(), null, String.format("Wiped recording data in %s on %s", voiceChannel.getName(), voiceChannel.getGuild().getName()));
    uncompIndex = 0;
    compIndex = 0;
    uncompUserIndex = 0;
    compUserIndex = 0;

    compUserVoiceData = new byte[1024 * 1024 / 2];
    compVoiceData = new byte[1024 * 1024 / 2];
    System.gc();
  }


  public byte[] getUncompromisedVoice(int time) {
    canReceive = false;

    if (time > PCM_MINS * 60 * 2) {     //2 mins
      time = (int) (PCM_MINS * 60 * 2);
    }
    int requestSize = 3840 * 50 * time;
    byte[] voiceData = new byte[requestSize];

    for (int i = 0; i < voiceData.length; i++) {
      if (uncompIndex + i < voiceData.length)
        voiceData[i] = uncompVoiceData[uncompIndex + i];
      else
        voiceData[i] = uncompVoiceData[uncompIndex + i - voiceData.length];
    }

    wipeMemory();
    canReceive = true;
    return voiceData;
  }

}