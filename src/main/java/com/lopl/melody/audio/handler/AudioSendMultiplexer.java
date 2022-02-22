package com.lopl.melody.audio.handler;

import net.dv8tion.jda.api.audio.AudioSendHandler;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

/**
 * This class multiplexes multiple AudioSendHandler.
 * There is currently only one for the music player. But there might be more
 * for i.e. a Soundboard, a TTS player etc.
 * The active SendHandler is determined by a static mode.
 * Only one currently is the switch mode, which determines the provider with a priority.
 */
public class AudioSendMultiplexer implements AudioSendHandler {

  /**
   * The multiplex mode
   */
  private final SendMultiplex sendMultiplex;

  /**
   * The active provider or the last one if no one is providing
   */
  private AudioSendHandler currentProvider;

  /**
   * Constructor with multiple provider
   * @param provider a set of AudioSendHandler
   */
  public AudioSendMultiplexer(AudioSendHandler... provider) {
    this.sendMultiplex = SendMultiplex.Switch(provider);
  }

  /**
   * This method is called from the superclass.
   * It determines the active provider
   * @return a boolean that indicates if any of the providers want to provide
   */
  @Override
  public boolean canProvide() {
    if (sendMultiplex.mode == SendMultiplex.MultiplexMode.Switch) {
      for (AudioSendHandler sendHandler : sendMultiplex.handlers) {
        if (sendHandler.canProvide()) {
          currentProvider = sendHandler;
          return true;
        }
      }
    }
    return false;
  }

  /**
   * this method requests 20ms of audio.
   * The current provider should provide this.
   * @return 20ms of audio in a ByteBuffer.
   */
  @Nullable
  @Override
  public ByteBuffer provide20MsAudio() {
    return currentProvider.provide20MsAudio(); //TODO: NPE
  }

  @Override
  public boolean isOpus() {
    return currentProvider.isOpus();
  }


  /**
   * This class manages the multiplex mode.
   * It is currently under construction.
   */
  public static class SendMultiplex {
    public MultiplexMode mode = MultiplexMode.None;
    public AudioSendHandler[] handlers;
    float blendBalance;

    private SendMultiplex() {
    }

    public static SendMultiplex None() {
      return new SendMultiplex();
    }

    public static SendMultiplex Switch(AudioSendHandler... sendHandler) {
      if (sendHandler == null) {
        throw new RuntimeException("Send handler must not be null.");
      }
      SendMultiplex m = new SendMultiplex();
      m.handlers = sendHandler;
      m.mode = MultiplexMode.Switch;
      return m;
    }

    /*
     * Blend ratio between 0 and 1
     */
    public static SendMultiplex Blend(float blendRatio, AudioSendHandler... sendHandlers) {
      throw new UnsupportedOperationException("Blend mode is not supported yet.");

//                if(blendRatio < 0 || blendRatio > 1) {
//                    throw new RuntimeException("Blend ratio must be between 0 and 1.");
//                } else if(sendHandlers == null || sendHandlers.length == 0) {
//                    throw new RuntimeException("Must provide at least one audio send handler.");
//                }
//
//                SendMultiplex m = new SendMultiplex();
//                m.handlers = sendHandlers;
//                m.mode = MultiplexMode.Blend;
//                m.blendBalance = blendRatio;
//                return m;
    }

    public enum MultiplexMode {
      None,
      Switch,
      Blend
    }
  }
}
