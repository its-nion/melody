package com.lopl.melody.audio.provider.spotify;

import com.lopl.melody.audio.provider.TrackDataLoader;
import com.lopl.melody.utils.StringSimilarity;
import com.lopl.melody.utils.embed.EmbedError;
import com.lopl.melody.utils.embed.ReactionEmoji;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SpotifyMessageDisplayer {

  private final SpotifyMessage spotifyMessage;

  public SpotifyMessageDisplayer(SpotifyMessage spotifyMessage) {
    this.spotifyMessage = spotifyMessage;
  }

  private Message getMessage() {
    return spotifyMessage.getMessage();
  }

  public void show() {
    int index = spotifyMessage.index;
    if (spotifyMessage.tracks != null) {
      if (spotifyMessage.tracks.length != 0) showTrack(index);
      else showNoResults();
    } else if (spotifyMessage.playlists != null) {
      if (spotifyMessage.playlists.length != 0) showPlaylist(index);
      else showNoResults();
    } else if (spotifyMessage.albums != null) {
      if (spotifyMessage.albums.length != 0) showAlbum(index);
      else showNoResults();
    }
  }


  private void showPlaylist(int index) {
    Message message = getMessage();
    PlaylistSimplified playlist = spotifyMessage.playlists[index];
    message.clearReactions().queue();
    EmbedBuilder eb = new EmbedBuilder();

    // author
    eb.setAuthor(playlist.getOwner().getDisplayName());

    // title
    eb.setTitle(playlist.getName());

    // image
    if (playlist.getImages().length != 0)
      eb.setThumbnail(playlist.getImages()[0].getUrl());

    // content
    eb.appendDescription("**Tracks:**\n");
    Track[] tracks = Spotify.getPlaylistsTracks(playlist.getId());
    for (int i = 0; i < 5; i++) {
      if (i >= tracks.length) break;
      Track track = tracks[i];
      eb.appendDescription(track.getArtists()[0].getName() + " - " + track.getName() + "\n");
    }
    if (tracks.length >= 5) {
      eb.appendDescription((tracks.length - 5) + " more");
    }

    // length
    eb.setFooter(
        "Songs: " + tracks.length + "\n" +
            "Duration: " + new SimpleDateFormat("hh:mm:ss").format(new Date(Arrays.stream(tracks).mapToInt(Track::getDurationMs).sum())) + "\n" +
            "Page: " + (index + 1) + " / " + spotifyMessage.playlists.length,

        // player icon
        ReactionEmoji.SPOTIFY_LINK);

    // show
    List<MessageEmbed> embeds = message.getEmbeds();
    ArrayList<MessageEmbed> newEmbeds = newArrayList(embeds);
    newEmbeds.set(embeds.size() - 1, eb.build());
    message.editMessageEmbeds(newEmbeds).queue();
  }

  private void showTrack(int index) {
    Message message = getMessage();
    Track track = spotifyMessage.tracks[index];
    message.clearReactions().queue();
    EmbedBuilder eb = new EmbedBuilder();

    // author
    eb.setAuthor(track.getArtists()[0].getName());

    // title
    eb.setTitle(track.getName());

    // image
    if (track.getAlbum().getImages().length >= 1)
      eb.setThumbnail(track.getAlbum().getImages()[0].getUrl());

    // page + duration
    eb.setFooter(
        "Duration: " + new SimpleDateFormat("mm:ss").format(new Date(track.getDurationMs())) + "\n" +
            "Page: " + (index + 1) + " / " + spotifyMessage.tracks.length,

        // player icon
        ReactionEmoji.SPOTIFY_LINK);

    //album
    eb.appendDescription("Album: " + track.getAlbum().getName() + "\n");
    eb.appendDescription("Year: " + track.getAlbum().getReleaseDate().split("-")[0] + "\n");

    //popularity
    eb.appendDescription("Popularity: " + track.getPopularity() + "\n");

    // loaded song
    TrackDataLoader dataManager = TrackDataLoader.getInstance();
    dataManager.retrieveTrackInfo(getMessage().getGuild(), "ytsearch:" + track.getArtists()[0].getName() + "-" + track.getName(), loadedTrack -> {
      int closeness = (int) (StringSimilarity.similarity(loadedTrack, track) * 100);
      eb.appendDescription("Public Song similarity: " + closeness + "%\n");
      eb.appendDescription("Public platform: Youtube");
      List<MessageEmbed> embeds = message.getEmbeds();
      ArrayList<MessageEmbed> newEmbeds = newArrayList(embeds);
      newEmbeds.set(embeds.size() - 1, eb.build());
      message.editMessageEmbeds(newEmbeds).queue();
    });

    // show
    List<MessageEmbed> embeds = message.getEmbeds();
    ArrayList<MessageEmbed> newEmbeds = newArrayList(embeds);
    newEmbeds.set(embeds.size() - 1, eb.build());
    message.editMessageEmbeds(newEmbeds).queue();
  }

  private void showAlbum(int index) {
    Message message = getMessage();
    AlbumSimplified album = spotifyMessage.albums[index];
    message.clearReactions().queue();
    EmbedBuilder eb = new EmbedBuilder();

    // author
    eb.setAuthor("Playlist from " + album.getArtists()[0].getName());

    // title
    eb.setTitle(album.getName());

    // image
    if (album.getImages().length != 0)
      eb.setThumbnail(album.getImages()[0].getUrl());

    // content
    eb.appendDescription("**Tracks:**\n");
    Track[] tracks = Spotify.getAlbumTracks(album.getId());
    for (int i = 0; i < 5; i++) {
      if (i >= tracks.length) break;
      Track track = tracks[i];
      eb.appendDescription(track.getArtists()[0].getName() + " - " + track.getName() + "\n");
    }
    if (tracks.length >= 5) {
      eb.appendDescription((tracks.length - 5) + " more");
    }

    // length
    eb.setFooter(
        "Songs: " + tracks.length + "\n" +
            "Duration: " + new SimpleDateFormat("hh:mm:ss").format(new Date(Arrays.stream(tracks).mapToInt(Track::getDurationMs).sum())) + "\n" +
            "Page: " + (index + 1) + " / " + spotifyMessage.albums.length,

        // player icon
        ReactionEmoji.SPOTIFY_LINK);

    // show
    List<MessageEmbed> embeds = message.getEmbeds();
    ArrayList<MessageEmbed> newEmbeds = newArrayList(embeds);
    newEmbeds.set(embeds.size() - 1, eb.build());
    message.editMessageEmbeds(newEmbeds).queue();
  }

  private void showNoResults() {
    Message message = getMessage();
    message.clearReactions().queue();
    getMessage().editMessageEmbeds(EmbedError.with("No results found")).queue();
  }

  private ArrayList<MessageEmbed> newArrayList(List<MessageEmbed> embeds) {
    return new ArrayList<>(embeds);
  }
}
