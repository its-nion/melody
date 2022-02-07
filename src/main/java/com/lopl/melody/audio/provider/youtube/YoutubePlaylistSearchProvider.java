package com.lopl.melody.audio.provider.youtube;

import com.sedmelluq.discord.lavaplayer.source.youtube.BaseYoutubeHttpContextFilter;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchResultLoader;
import com.sedmelluq.discord.lavaplayer.tools.ExceptionTools;
import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser;
import com.sedmelluq.discord.lavaplayer.tools.http.ExtendedHttpConfigurable;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterfaceManager;
import com.sedmelluq.discord.lavaplayer.track.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubePlaylistSearchProvider implements YoutubeSearchResultLoader {
  private static final Logger log = LoggerFactory.getLogger(com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider.class);
  private static final String WATCH_URL_PREFIX = "https://www.youtube.com/watch?v=";
  private final HttpInterfaceManager httpInterfaceManager = HttpClientTools.createCookielessThreadLocalManager();
  private final Pattern polymerInitialDataRegex = Pattern.compile("(window\\[\"ytInitialData\"]|var ytInitialData)\\s*=\\s*(.*);");

  public YoutubePlaylistSearchProvider() {
    this.httpInterfaceManager.setHttpContextFilter(new BaseYoutubeHttpContextFilter());
  }

  public ExtendedHttpConfigurable getHttpConfiguration() {
    return this.httpInterfaceManager;
  }

  public AudioItem loadSearchResult(String query, Function<AudioTrackInfo, AudioTrack> trackFactory) {
    log.debug("Performing a search with query {}", query);

    try {
      HttpInterface httpInterface = this.httpInterfaceManager.getInterface();
      Throwable var4 = null;

      AudioItem audioItem;
      try {
        URI url = new URIBuilder("https://www.youtube.com/results")
            .addParameter("search_query", query)
            .addParameter("hl", "en")
            .addParameter("persist_hl", "1")
            .addParameter("sp", "EgIQAw%3D%3D").build();
        log.debug(url.toString());
        CloseableHttpResponse response = httpInterface.execute(new HttpGet(url));
        Throwable var7 = null;

        try {
          HttpClientTools.assertSuccessWithContent(response, "search response");
          Document document = Jsoup.parse(response.getEntity().getContent(), StandardCharsets.UTF_8.name(), "");
          audioItem = this.extractSearchResults(document, query, trackFactory);
        } catch (Throwable var34) {
          var7 = var34;
          throw var34;
        } finally {
          if (response != null) {
            if (var7 != null) {
              try {
                response.close();
              } catch (Throwable var33) {
                var7.addSuppressed(var33);
              }
            } else {
              response.close();
            }
          }

        }
      } catch (Throwable var36) {
        var4 = var36;
        throw var36;
      } finally {
        if (httpInterface != null) {
          if (var4 != null) {
            try {
              httpInterface.close();
            } catch (Throwable var32) {
              var4.addSuppressed(var32);
            }
          } else {
            httpInterface.close();
          }
        }

      }
      return audioItem;
    } catch (Exception var38) {
      throw ExceptionTools.wrapUnfriendlyExceptions(var38);
    }
  }

  private AudioItem extractSearchResults(Document document, String query, Function<AudioTrackInfo, AudioTrack> trackFactory) {
    List<AudioTrack> tracks;
    try {
      tracks = this.polymerExtractTracks(document, trackFactory);
    } catch (IOException var10) {
      return null;
    }
    return tracks.isEmpty() ? AudioReference.NO_TRACK : new BasicAudioPlaylist("Search results for: " + query, tracks, null, true);
  }

  private List<AudioTrack> polymerExtractTracks(Document document, Function<AudioTrackInfo, AudioTrack> trackFactory) throws IOException {
    Matcher matcher = this.polymerInitialDataRegex.matcher(document.outerHtml());
    if (!matcher.find()) {
      log.warn("Failed to match ytInitialData JSON object");
      return Collections.emptyList();
    } else {
      JsonBrowser jsonBrowser = JsonBrowser.parse(matcher.group(2));
      ArrayList<AudioTrack> list = new ArrayList<>();
      List<JsonBrowser> contents = jsonBrowser.get("contents").get("twoColumnSearchResultsRenderer").get("primaryContents").get("sectionListRenderer").get("contents").index(0).get("itemSectionRenderer").get("contents").values();
      contents.forEach((json) -> {
        AudioTrack track = this.extractPolymerData(json, trackFactory);
        if (track != null) {
          list.add(track);
        }
      });
      return list;
    }
  }

  private AudioTrack extractPolymerData(JsonBrowser json, Function<AudioTrackInfo, AudioTrack> trackFactory) {
    JsonBrowser renderer = json.get("playlistRenderer");
    if (renderer.isNull()) {
      return null;
    } else {
      String title = renderer.get("title").get("simpleText").text();
      String author = renderer.get("shortBylineText").get("runs").index(0).get("text").text();
      String songCount = renderer.get("videoCount").text();
      int songs = Integer.parseInt(songCount);
      String plId = renderer.get("playlistId").text();
      String videoId = renderer.get("navigationEndpoint").get("watchEndpoint").get("videoId").text();
      AudioTrackInfo info = new AudioTrackInfo(title, author, songs, plId, false, WATCH_URL_PREFIX + videoId + "&list=" + plId);
      return trackFactory.apply(info);
    }
  }
}
