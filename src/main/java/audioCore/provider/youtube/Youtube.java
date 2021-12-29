package audioCore.provider.youtube;

import audioCore.provider.MusicDataSearcher;
import audioCore.provider.spotify.Spotify;
import audioCore.provider.youtube.YoutubeButtonMessage;
import audioCore.util.MessageStore;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import utils.Logging;

public class Youtube implements MusicDataSearcher {
  @Override
  public void search(@NotNull SlashCommandEvent event, @NotNull String search, Message message) {
    searchYoutube(event, search, message);
  }

  public static void searchYoutube(@NotNull SlashCommandEvent event, @NotNull String search, Message message){
    Logging.debug(Youtube.class, event.getGuild(), null, "Youtube Re-Loaded");
    YoutubeButtonMessage youtubeButtonMessage;
    Logging.debug(Spotify.class, event.getGuild(), event.getMember(), "Searching on Spotify for Tracks with: " + removeAll(search, "ytsearch:"));
    AudioTrack[] tracks = getTracks("ytsearch:" + search, new String[0]);
    youtubeButtonMessage = new YoutubeButtonMessage(message, tracks);
    MessageStore.saveMessage(youtubeButtonMessage);
//    if (youtubeButtonMessage != null)
      youtubeButtonMessage.show();
  }

  private static AudioTrack[] getTracks(String name, String[] ignores){
    for (String ignored : ignores)
      name = name.replaceAll(ignored, "");

    YoutubeAudioSourceManager yasm = new YoutubeAudioSourceManager(true);
    AudioItem result = yasm.loadItem(null, new AudioReference("ytsearch:" + name, null));
    if (result instanceof BasicAudioPlaylist){
      BasicAudioPlaylist resultPlaylist = (BasicAudioPlaylist) result;
      return resultPlaylist.getTracks().toArray(AudioTrack[]::new);
    }
    return null;
  }

  private static String removeAll(String word, String... ignores) {
    for (String ignored : ignores)
      word = word.replaceAll(ignored, "");
    return word.strip();
  }
}
