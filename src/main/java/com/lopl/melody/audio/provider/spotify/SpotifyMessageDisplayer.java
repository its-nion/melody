package com.lopl.melody.audio.provider.spotify;

import com.lopl.melody.audio.provider.TrackDataLoader;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import com.lopl.melody.utils.StringSimilarity;
import com.lopl.melody.utils.embed.EmbedError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SpotifyMessageDisplayer {

  private final SpotifyMessage spotifyMessage;
  private final Track[] tracks;
  private final PlaylistSimplified[] playlists;

  public SpotifyMessageDisplayer(SpotifyMessage spotifyMessage) {
    this.spotifyMessage = spotifyMessage;
    this.tracks = spotifyMessage.tracks;
    this.playlists = spotifyMessage.playlists;
  }

  private Message getMessage() {
    return spotifyMessage.getMessage();
  }

  public void show() {
    int index = spotifyMessage.index;
    if (tracks != null) {
      if (tracks.length != 0) showTrack(index);
      else showNoResults();
    } else if (playlists != null) {
      if (playlists.length != 0) showPlaylist(index);
      else showNoResults();
    }
  }


  private void showPlaylist(int index) {
    Message message = getMessage();
    PlaylistSimplified playlist = playlists[index];
    message.clearReactions().queue();
    EmbedBuilder eb = new EmbedBuilder();

    // author
    eb.setAuthor(playlists[index].getOwner().getDisplayName());

    // title
    eb.setTitle(playlists[index].getName());

    // image
    if (playlists[index].getImages().length != 0)
      eb.setThumbnail(playlists[index].getImages()[0].getUrl());

    // content
    eb.appendDescription("**Tracks:**\n");
    PlaylistTrack[] tracks = Spotify.getPlaylistsTracks(playlist.getId());
    for (int i = 0; i < 5; i++) {
      if (i >= tracks.length) break;
      PlaylistTrack track = tracks[i];
      eb.appendDescription(track.getTrack().getArtists()[0].getName() + " - " + track.getTrack().getName() + "\n");
    }
    if (tracks.length >= 5){
      eb.appendDescription((tracks.length - 5) + " more");
    }

    // length
    eb.setFooter(
        "Songs: " + tracks.length + "\n" +
        "Duration: " + new SimpleDateFormat("hh:mm:ss").format(new Date(Arrays.stream(tracks).mapToInt(t -> t.getTrack().getDurationMs()).sum())) + "\n" +
        "Page: " + (index+1) + " / " + tracks.length,

    // player icon
    "https://cdn.discordapp.com/emojis/925826109650665473.png?size=96");

    // show
    List<MessageEmbed> embeds = message.getEmbeds();
    ArrayList<MessageEmbed> newEmbeds = newArrayList(embeds);
    newEmbeds.set(embeds.size() -1, eb.build());
    message.editMessageEmbeds(newEmbeds).queue();
  }

  private void showTrack(int index) {
    Message message = getMessage();
    Track track = tracks[index];
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
            "Page: " + (index+1) + " / " + tracks.length,

    // player icon
        "https://cdn.discordapp.com/emojis/925826109650665473.png?size=96");

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
      newEmbeds.set(embeds.size() -1, eb.build());
      message.editMessageEmbeds(newEmbeds).queue();
    });

    // show
    List<MessageEmbed> embeds = message.getEmbeds();
    ArrayList<MessageEmbed> newEmbeds = newArrayList(embeds);
    newEmbeds.set(embeds.size() -1, eb.build());
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
