package com.lopl.melody.audio.provider.youtube;

import com.lopl.melody.utils.annotation.NotYetImplemented;
import com.lopl.melody.utils.embed.EmbedError;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class YoutubeMessageDisplayer {

  private final YoutubeMessage youtubeMessage;
  private final AudioTrack[] tracks;
  private final PlaylistSimplified[] playlists;

  public YoutubeMessageDisplayer(YoutubeMessage spotifyMessage) {
    this.youtubeMessage = spotifyMessage;
    this.tracks = spotifyMessage.tracks;
    this.playlists = spotifyMessage.playlists;
  }

  private Message getMessage() {
    return youtubeMessage.getMessage();
  }

  public void show() {
    int index = youtubeMessage.index;

    if (tracks != null) {
      if (tracks.length != 0)
        showTrack(index);
      else
        showNoResults();
    } else if (playlists != null) {
      if (playlists.length != 0)
        showPlaylist(index);
      else
        showNoResults();
    }
  }


  @NotYetImplemented
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
//    PlaylistTrack[] tracks = Spotify.getPlaylistsTracks(playlist.getId());
    for (int i = 0; i < 5; i++) {
      if (i >= tracks.length) break;
//      PlaylistTrack track = tracks[i];
//      eb.appendDescription(track.getTrack().getArtists()[0].getName() + " - " + track.getTrack().getName() + "\n");
    }
    if (tracks.length >= 5) {
      eb.appendDescription((tracks.length - 5) + " more");
    }

    // length
//    eb.setFooter("Songs: " + tracks.length + "\n" + "Duration: " + new SimpleDateFormat("hh:mm:ss").format(new Date(Arrays.stream(tracks).mapToInt(t -> t.getTrack().getDurationMs()).sum())));

    // show
    List<MessageEmbed> embeds = message.getEmbeds();
    ArrayList<MessageEmbed> newEmbeds = newArrayList(embeds);
    newEmbeds.set(embeds.size() - 1, eb.build());
    message.editMessageEmbeds(newEmbeds).queue();
  }

  private void showTrack(int index) {
    Message message = getMessage();
    AudioTrack track = tracks[index];
    message.clearReactions().queue();
    EmbedBuilder eb = new EmbedBuilder();

    // author
    eb.setAuthor(track.getInfo().author);

    // title
    eb.setTitle(track.getInfo().title);

    // image
    eb.setThumbnail(getThumbnail(track.getInfo().uri));

    // length


    // page + duration
    eb.setFooter(
        "Duration: " + new SimpleDateFormat("mm:ss").format(new Date(track.getDuration())) + "\n" +
            "Page: " + (index + 1) + " / " + tracks.length,

        // icon
        "https://cdn.discordapp.com/emojis/925827032485621791.png?size=96");


    // show
    List<MessageEmbed> embeds = message.getEmbeds();
    ArrayList<MessageEmbed> newEmbeds = newArrayList(embeds);
    newEmbeds.set(embeds.size() - 1, eb.build());
    message.editMessageEmbeds(newEmbeds).queue();

  }

  private String getThumbnail(String uri) {
    // i.e. https://www.youtube.com/watch?v=dQw4w9WgXcQ
    uri = uri.replaceAll("//youtube", "//img.youtube");
    uri = uri.replaceAll("www", "img");
    uri = uri.replaceAll("watch\\?v=", "vi/");
    uri = uri + "/hqdefault.jpg";
    // i.e. https://img.youtube.com/vi/dQw4w9WgXcQ/hqdefault.jpg
    return uri;
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
