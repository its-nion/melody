package com.lopl.melody.audio.provider.youtube;

import com.lopl.melody.audio.provider.MusicDataSearcher;
import com.lopl.melody.utils.message.MessageStore;
import com.sedmelluq.discord.lavaplayer.source.youtube.*;
import com.sedmelluq.discord.lavaplayer.track.*;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Youtube implements MusicDataSearcher<AudioTrack, AudioPlaylist, AudioPlaylist, String> {

  @Override
  public AudioTrack[] searchTracks(@NotNull String name) {
    YoutubeAudioSourceManager yasm = new YoutubeAudioSourceManager(true);
    AudioItem result = yasm.loadItem(null, new AudioReference("ytsearch:" + name, null));
    if (result instanceof BasicAudioPlaylist) {
      BasicAudioPlaylist resultPlaylist = (BasicAudioPlaylist) result;
      return resultPlaylist.getTracks().toArray(AudioTrack[]::new);
    }
    return null;
  }

  @Override
  public AudioPlaylist[] searchPlaylists(@NotNull String name){
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

  @Override
  public AudioPlaylist[] searchUser(@NotNull String name){
    YoutubeAudioSourceManager yasm = new YoutubeAudioSourceManager(true, new DefaultYoutubeTrackDetailsLoader(), new YoutubeUserSearchProvider(), new YoutubeSearchMusicProvider(), new YoutubeSignatureCipherManager(), new DefaultYoutubePlaylistLoader(), new DefaultYoutubeLinkRouter(), new YoutubeMixProvider());
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

  /**
   * It is way more efficient to call searchUser before calling searchUserName
   * @param search the search term
   * @return cached userName or a new request
   */
  @Override
  public String searchUserName(@NotNull String search) {
    return YoutubeUserSearchProvider.getChannelName(search);
  }

  @Override
  public void onUserSearch(AudioPlaylist[] playlists, String userName, Message message) {
    YoutubeMessage youtubeMessage = new YoutubeMessage(message, playlists, userName);
    MessageStore.saveMessage(youtubeMessage);
    youtubeMessage.show();
  }

  @Override
  public void onPlaylistSearch(AudioPlaylist[] playlists, Message message) {
    YoutubeMessage youtubeMessage = new YoutubeMessage(message, playlists);
    MessageStore.saveMessage(youtubeMessage);
    youtubeMessage.show();
  }

  @Override
  public void onTrackSearch(AudioTrack[] tracks, Message message) {
    YoutubeMessage youtubeMessage = new YoutubeMessage(message, tracks);
    MessageStore.saveMessage(youtubeMessage);
    youtubeMessage.show();
  }
}
