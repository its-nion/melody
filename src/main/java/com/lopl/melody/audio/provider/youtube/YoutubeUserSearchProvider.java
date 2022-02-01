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
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeUserSearchProvider implements YoutubeSearchResultLoader {
  private static final Logger log = LoggerFactory.getLogger(com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider.class);
  private static final String WATCH_URL_PREFIX = "https://www.youtube.com/watch?v=";
  private final HttpInterfaceManager httpInterfaceManager = HttpClientTools.createCookielessThreadLocalManager();
  private final Pattern polymerInitialDataRegex = Pattern.compile("(window\\[\"ytInitialData\"]|var ytInitialData)\\s*=\\s*(.*);");
  private static final HashMap<String, String> channelNames = new HashMap<>();

  public YoutubeUserSearchProvider() {
    this.httpInterfaceManager.setHttpContextFilter(new BaseYoutubeHttpContextFilter());
  }

  public static String getChannelName(String query){
    if (channelNames.containsKey(query)) return channelNames.get(query);
    new Youtube().searchUser(query);
    return channelNames.get(query);
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
            .addParameter("sp", "EgIQAg%3D%3D").build();
        log.debug(url.toString());
        CloseableHttpResponse response = httpInterface.execute(new HttpGet(url));
        Throwable var7 = null;

        try {
          HttpClientTools.assertSuccessWithContent(response, "search response");
          Document document = Jsoup.parse(response.getEntity().getContent(), StandardCharsets.UTF_8.name(), "");
          String channelUri = this.extractSearchResults(document, query);
          audioItem = loadChannelResult(channelUri, trackFactory);
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

  public AudioItem loadChannelResult(String uri, Function<AudioTrackInfo, AudioTrack> trackFactory) {
    log.debug("Performing a search with query {}", uri);

    try {
      HttpInterface httpInterface = this.httpInterfaceManager.getInterface();
      Throwable var4 = null;

      AudioItem audioItem;
      try {
        URI url = new URIBuilder(uri).build();
        log.debug(url.toString());
        CloseableHttpResponse response = httpInterface.execute(new HttpGet(url));
        Throwable var7 = null;

        try {
          HttpClientTools.assertSuccessWithContent(response, "search response");
          Document document = Jsoup.parse(response.getEntity().getContent(), StandardCharsets.UTF_8.name(), "");
          audioItem = this.extractChannelResults(document, uri, trackFactory);
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

  private String extractSearchResults(Document document, String query) {
    try {
      return polymerExtractChannelUri(document, query);
    } catch (IOException var10) {
      return null;
    }
  }

  private AudioItem extractChannelResults(Document document, String query, Function<AudioTrackInfo, AudioTrack> trackFactory) {
    List<AudioTrack> tracks;
    try {
      tracks = this.polymerExtractChannelPlaylists(document, trackFactory);
    } catch (IOException var10) {
      throw new RuntimeException(var10);
    }
    return tracks.isEmpty() ? AudioReference.NO_TRACK : new BasicAudioPlaylist("Search results for: " + query, tracks, null, true);
  }

  private String polymerExtractChannelUri(Document document, String query) throws IOException {
    Matcher matcher = this.polymerInitialDataRegex.matcher(document.outerHtml());
    if (!matcher.find()) {
      log.warn("Failed to match ytInitialData JSON object");
      return null;
    } else {
      JsonBrowser jsonBrowser = JsonBrowser.parse(matcher.group(2));
      List<JsonBrowser> contents = jsonBrowser.get("contents").get("twoColumnSearchResultsRenderer").get("primaryContents").get("sectionListRenderer").get("contents").index(0).get("itemSectionRenderer").get("contents").values();
      String channelUrlTail = contents.get(0).get("channelRenderer").get("navigationEndpoint").get("commandMetadata").get("webCommandMetadata").get("url").text();
      String channelName = contents.get(0).get("channelRenderer").get("title").get("simpleText").text();
      if (channelUrlTail == null) {
        channelUrlTail = contents.get(1).get("channelRenderer").get("navigationEndpoint").get("commandMetadata").get("webCommandMetadata").get("url").text();
        channelName = contents.get(1).get("channelRenderer").get("title").get("simpleText").text();
      }
      channelNames.put(query, channelName);
      if (channelUrlTail == null) return null;
      return "https://www.youtube.com" + channelUrlTail + "/playlists";
    }
  }

  private List<AudioTrack> polymerExtractChannelPlaylists(Document document, Function<AudioTrackInfo, AudioTrack> trackFactory) throws IOException {
    Matcher matcher = this.polymerInitialDataRegex.matcher(document.outerHtml());
    if (!matcher.find()) {
      log.warn("Failed to match ytInitialData JSON object");
      return Collections.emptyList();
    } else {
      JsonBrowser jsonBrowser = JsonBrowser.parse(matcher.group(2));
      ArrayList<AudioTrack> list = new ArrayList<>();
      List<JsonBrowser> rows = jsonBrowser.get("contents").get("twoColumnBrowseResultsRenderer").get("tabs").index(2).get("tabRenderer").get("content").get("sectionListRenderer").get("contents").values();
      if (rows.size() > 5) rows = rows.subList(0, 5);
      rows.forEach((row) -> {
        if (list.size() >= 20) return;
        List<JsonBrowser> pls = row.get("itemSectionRenderer").get("contents").index(0).get("shelfRenderer").get("content").get("horizontalListRenderer").get("items").values();
        pls.forEach(json -> {
          if (list.size() >= 20) return;
          AudioTrack track = this.extractPolymerData(json, trackFactory);
          if (track != null) {
            list.add(track);
          }
        });
      });
      return list;
    }
  }

  private AudioTrack extractPolymerData(JsonBrowser json, Function<AudioTrackInfo, AudioTrack> trackFactory) {
    JsonBrowser renderer = json.get("gridPlaylistRenderer");
    if (renderer.isNull()) {
      return null;
    } else {
      String title = renderer.get("title").get("runs").index(0).get("text").text();
//      String author = renderer.get("shortBylineText").get("runs").index(0).get("text").text();
      String songCount = renderer.get("videoCountShortText").get("simpleText").text();
      int songs = Integer.parseInt(songCount);
      String plId = renderer.get("playlistId").text();
      String videoId = renderer.get("navigationEndpoint").get("watchEndpoint").get("videoId").text();
      AudioTrackInfo info = new AudioTrackInfo(title, " ", songs, plId, false, WATCH_URL_PREFIX + videoId + "&list=" + plId);
      return trackFactory.apply(info);
    }
  }
}
