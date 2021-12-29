package utils;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;

public class YTTest {

  public static void main(String[] args) {
    String search = "NF";
    YoutubeAudioSourceManager yasm = new YoutubeAudioSourceManager(true);
    AudioItem result = yasm.loadItem(null, new AudioReference("ytsearch:" + search, null));
    System.out.println(result);
  }
}
