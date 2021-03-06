package com.lopl.melody.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.lopl.melody.audio.handler.MusicLoader;
import com.lopl.melody.audio.provider.MusicDataSearcher;
import com.lopl.melody.audio.provider.spotify.Spotify;
import com.lopl.melody.audio.provider.spotify.SpotifyMessage;
import com.lopl.melody.audio.provider.youtube.Youtube;
import com.lopl.melody.audio.provider.youtube.YoutubeMessage;
import com.lopl.melody.audio.util.AudioStateChecks;
import com.lopl.melody.settings.GuildSettings;
import com.lopl.melody.settings.SettingsManager;
import com.lopl.melody.settings.items.MusicPlayerProvider;
import com.lopl.melody.slash.SlashCommand;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.embed.EmbedError;
import com.lopl.melody.utils.embed.ReactionEmoji;
import com.lopl.melody.utils.message.MessageStore;
import com.lopl.melody.utils.message.SavedMessage;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import se.michaelthelin.spotify.model_objects.specification.Track;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Play Command
 * General functionality is defined here
 * When entering this command in a guild chat, you are able to search for a song, that is going to be played in your
 * Voice Channel.
 * Possible usage of the command:
 * <p>/play with a link: When entering "/play" followed by a link, this link will be played directly</p>
 * <p>/play with a query: When entering "/play" followed by any string, the string will be searched on Spotify.
 * The results will be displayed in a {@link MessageEmbed}. You can select from here what song to play with provided {@link Button}s.
 * Button Click will be handled in the {@link #clicked(ButtonClickEvent, boolean)}.</p>
 * <p>/play with type and query: You can enter a search type for your query right before your query.
 * Possible types are "playlist", "user" and "track".</p>
 */
public class Play extends SlashCommand {

  /**
   * IDS for command options or buttons
   */
  public static final String URL_TYPE_SEARCH = "search";
  public static final String NEXT = "next";
  public static final String PREVIOUS = "previous";
  public static final String PLAY = "play";

  /**
   * Constructor for a Play Command Object. This is instantiated when building the Command Handler or for temporary operations
   * <p>
   * {@link #name} The name of the command. The string you have to type after the '/'. i.e. /play
   * {@link #category} The category of the command. Commands will be sorted by that
   * {@link #help} The help message, in case someone types /help [command]
   * {@link #description} This description will be shown together with the command name in the command preview
   */
  public Play() {
    super.name = "play";
    super.category = new Command.Category("Sound");
    super.help = """
        /play [link] : plays the song/playlist of the link. possible links from youtube, soundcloud or bandcamp
        /play playlist [search] : searches for a playlist with spotify. react with eiter the "arrow left" or the "arrow right" to navigate threw the results, react with "musical_note" to play the playlist or react with "+" to queue the playlist. aliases: pl, list
        /play song [search] : searches for a song with spotify. react with eiter the "arrow left" or the "arrow right" to navigate threw the results, react with "musical_note" to play the song or react with "+" to queue the song. aliases: track
        /play user [userID] : lists the users playlists on spotify. react with eiter the "arrow left" or the "arrow right" to navigate threw the playlists, react with "musical_note" to play the playlist or react with "+" to queue the playlist. aliases: account, member
        /play [search] : searches for answer of the type defined in the settings""";
    super.description = "Search for your favourite songs to jam to.";
  }

  /**
   * This will fire whenever a command reload is executed. this defines how the play command is build.
   *
   * @param cca the object the command is build with. you can modify it as you want.
   * @return the finished play command builder with an option for a search query
   */
  @Override
  protected CommandCreateAction onUpsert(CommandCreateAction cca) {
    return cca.addOption(OptionType.STRING, URL_TYPE_SEARCH, "URL or Type or Search", true);
  }

  /**
   * This will fire whenever a user enters /play ... in a guild textchannel.
   * If the requesting member is in a vc and the command is correct, the bot will join you and a list of possible search
   * answers are presented to you.
   * You can navigate with buttons and play your preferred songs
   *
   * @param event all the event data
   */
  @Override
  protected void execute(SlashCommandEvent event) {
    Logging.slashCommand(getClass(), event);

    if (event.getGuild() == null) {
      event.replyEmbeds(EmbedError.with("This command can only be executed in a server textchannel")).queue();
      return;
    }

    if (!AudioStateChecks.isMemberInVC(event)) {
      event.replyEmbeds(EmbedError.with("This Command requires **you** to be **connected to a voice channel**")).queue();
      return;
    }

    if (event.getOptions().isEmpty()) {
      event.replyEmbeds(EmbedError.with("Please provide some arguments, type /help play for further information")).queue();
      return;
    }

    Player player = getPlayer(event.getGuild());
    handlePlayCommand(event, event.getGuild(), player);
  }

  /**
   * This will fire whenever a user enters /play ... in a guild textchannel.
   * The requesting member is in a vc and the command is correct. Therefore the bot will join you and a list of
   * possible search answers are presented to you.
   * You can navigate with buttons and play your preferred songs
   *
   * @param event  all the event data
   * @param player the player that should be used
   */
  private void handlePlayCommand(SlashCommandEvent event, @Nonnull Guild guild, Player player) {
    AudioManager audio = guild.getAudioManager();
    List<MessageEmbed> embeds = null;
    if (!audio.isConnected())
      embeds = new Join().connectReturnEmbed(event);

    OptionMapping firstOption = event.getOption(URL_TYPE_SEARCH);
    assert firstOption != null;
    String firstArg = firstOption.getAsString();

    if (isUrl(firstArg)) {
      // URL
      new MusicLoader().loadURL(event.getTextChannel(), firstArg.strip());
    } else {
      // SEARCH
      // create buttons
      Button[] buttons = new Button[]{
          Button.secondary(PREVIOUS, Emoji.fromMarkdown(ReactionEmoji.ARROW_LEFT)).asDisabled(),
          Button.secondary(PLAY, Emoji.fromMarkdown(ReactionEmoji.MUSIC)),
          Button.secondary(NEXT, Emoji.fromMarkdown(ReactionEmoji.ARROW_RIGHT))
      };
      for (Button component : buttons)
        registerButton(component);

      // create embeds
      if (embeds == null) embeds = new ArrayList<>();
      embeds.add(new EmbedBuilder().setDescription("Loading " + player.toString() + "...").build());

      // create Message from embeds and buttons
      Message message = event
          .replyEmbeds(embeds)
          .addActionRow(buttons).complete().retrieveOriginal().complete();

      //Main.info(event, "Searching for songs: '" + firstArg + "' with: " + player.name(), Main.ANSI_BLUE);
      player.getProvider().search(event, firstArg, message);
    }
  }

  private boolean isUrl(String input) {
    try {
      new URL(input);
      return true;
    } catch (MalformedURLException ignored) {
      return false;
    }
  }

  /**
   * This will fire when a user clicked on a button regarding this command.
   * The Button has to be registered beforehand with {@link #registerButton(Button)}.
   * The regarding message will be searched and continued with {@link #executeButtonAction(ButtonClickEvent, Guild, SpotifyMessage)}
   *
   * @param event all the button click event data
   */
  @Override
  protected void clicked(ButtonClickEvent event, boolean anonymous) {
    Logging.button(getClass(), event);

    if (event.getGuild() == null) {
      event.replyEmbeds(EmbedError.with("This command can only be executed in a server textchannel")).queue();
      return;
    }

    for (SavedMessage message : MessageStore.allMessages()) {
      if (message.getMessageID() == event.getMessageIdLong() && message instanceof SpotifyMessage) {
        SpotifyMessage spotifyMessage = (SpotifyMessage) message;
        executeButtonAction(event, event.getGuild(), spotifyMessage);

        Button[] buttons = new Button[3];
        event.editComponents(ActionRow.of(
            buttons[0] = Button.secondary(PREVIOUS, Emoji.fromMarkdown(ReactionEmoji.ARROW_LEFT)).withDisabled(!spotifyMessage.hasPrevious()),
            buttons[1] = Button.secondary(PLAY, Emoji.fromMarkdown(ReactionEmoji.MUSIC)),
            buttons[2] = Button.secondary(NEXT, Emoji.fromMarkdown(ReactionEmoji.ARROW_RIGHT)).withDisabled(!spotifyMessage.hasNext())
        )).queue();
        for (Button component : buttons)
          registerButton(component);
        return;
      }

      if (message.getMessageID() == event.getMessageIdLong() && message instanceof YoutubeMessage) {
        YoutubeMessage youtubeMessage = (YoutubeMessage) message;
        executeButtonAction(event, event.getGuild(), youtubeMessage);

        Button[] buttons = new Button[3];
        event.editComponents(ActionRow.of(
            buttons[0] = Button.secondary(PREVIOUS, Emoji.fromMarkdown(ReactionEmoji.ARROW_LEFT)).withDisabled(!youtubeMessage.hasPrevious()),
            buttons[1] = Button.secondary(PLAY, Emoji.fromMarkdown(ReactionEmoji.MUSIC)),
            buttons[2] = Button.secondary(NEXT, Emoji.fromMarkdown(ReactionEmoji.ARROW_RIGHT)).withDisabled(!youtubeMessage.hasNext())
        )).queue();
        for (Button component : buttons)
          registerButton(component);
        return;
      }
    }
  }

  /**
   * After the correct saved data message has been found, regarding of what button was pressed, a different action
   * will be executed
   *
   * @param event          all the button click event data
   * @param spotifyMessage the found data message
   */
  private void executeButtonAction(ButtonClickEvent event, @Nonnull Guild guild, SpotifyMessage spotifyMessage) {
    if (event.getButton() == null || event.getButton().getId() == null) return;

    switch (event.getButton().getId()) {
      case PREVIOUS -> spotifyMessage.previous();
      case NEXT -> spotifyMessage.next();
      case PLAY -> queue(event, guild, spotifyMessage);
      default -> {
      }
    }
  }

  /**
   * After the correct saved data message has been found, regarding of what button was pressed, a different action
   * will be executed
   *
   * @param event          all the button click event data
   * @param youtubeMessage the found data message
   */
  private void executeButtonAction(ButtonClickEvent event, @Nonnull Guild guild, YoutubeMessage youtubeMessage) {
    if (event.getButton() == null || event.getButton().getId() == null) return;

    switch (event.getButton().getId()) {
      case PREVIOUS -> youtubeMessage.previous();
      case NEXT -> youtubeMessage.next();
      case PLAY -> queue(event, guild, youtubeMessage);
      default -> {
      }
    }
  }

  /**
   * This is queueing a track or other music object, that is selected in the current data message
   *
   * @param event          all the button click event data
   * @param guild          the NonNull Guild the command was executed in
   * @param spotifyMessage the found data message
   */
  private void queue(ButtonClickEvent event, @Nonnull Guild guild, SpotifyMessage spotifyMessage) {
    AudioManager audio = guild.getAudioManager();
    if (!audio.isConnected())
      new Join().connect(guild, event.getMember(), event.getTextChannel());

    if (spotifyMessage.tracks == null) {
      //PLAYLIST
      List<String> queries = new ArrayList<>();
      Track[] tracks = Spotify.getPlaylistsTracks(spotifyMessage.getCurrentPlaylist().getId());
      int maxLoadCount = 100;
      for (int i = 0; i < tracks.length && i < maxLoadCount; i++) {
        String artist = tracks[i].getArtists()[0].getName();
        String title = tracks[i].getName();
        String search = "ytsearch:" + artist + "-" + title;
        queries.add(search);
      }
      Logging.info(getClass(), event.getGuild(), event.getMember(), "Finding " + queries.size() + " Tracks on Youtube");
      new MusicLoader().loadMultiple(event.getTextChannel(), spotifyMessage.getCurrentPlaylist().getName(), queries.toArray(String[]::new));
    } else {
      //SINGLE
      Track track = spotifyMessage.getCurrentTrack();
      String url = "ytsearch:" + track.getArtists()[0].getName() + " - " + track.getName();
      Logging.debug(getClass(), event.getGuild(), event.getMember(), "Finding Track on Youtube with: " + url);
      new MusicLoader().loadOne(event.getTextChannel(), url);
    }
  }

  private void queue(ButtonClickEvent event, @Nonnull Guild guild, YoutubeMessage youtubeMessage) {
    AudioManager audio = guild.getAudioManager();
    if (!audio.isConnected())
      new Join().connect(guild, event.getMember(), event.getTextChannel());

    if (youtubeMessage.getTracks() == null) {
      //PLAYLIST
      List<String> queries = new ArrayList<>();
      AudioTrack[] tracks = youtubeMessage.getCurrentPlaylist().getTracks().toArray(new AudioTrack[0]);
      int maxLoadCount = 100;
      for (int i = 0; i < tracks.length && i < maxLoadCount; i++) {
        String search = tracks[i].getInfo().uri;
        queries.add(search);
      }
      Logging.info(getClass(), event.getGuild(), event.getMember(), "Finding " + queries.size() + " Tracks on Youtube");
      new MusicLoader().loadMultiple(event.getTextChannel(), youtubeMessage.getCurrentPlaylist().getName(), queries.toArray(String[]::new));
    } else {
      //SINGLE
      AudioTrack track = youtubeMessage.getCurrentTrack();
      String url = track.getInfo().uri;
      Logging.debug(getClass(), event.getGuild(), event.getMember(), "Loading Youtube Track: " + url);
      new MusicLoader().loadURL(event.getTextChannel(), url);
    }
  }

  private Player getPlayer(Guild guild) {
    GuildSettings guildSettings = SettingsManager.getInstance().getGuildSettings(guild);
    MusicPlayerProvider.Value provider = guildSettings.getSetting(MusicPlayerProvider.class).getValue();
    if (provider.isYoutube()) return Player.YOUTUBE;
    if (provider.isSpotify()) return Player.SPOTIFY;
    return Player.YOUTUBE; // <- default, this probably gets used never
  }

  private enum Player {
    YOUTUBE, SPOTIFY;

    private static String toCamelCase(final String init) {
      if (init == null)
        return null;

      final StringBuilder ret = new StringBuilder(init.length());

      for (final String word : init.split(" ")) {
        if (!word.isEmpty()) {
          ret.append(Character.toUpperCase(word.charAt(0)));
          ret.append(word.substring(1).toLowerCase());
        }
        if (!(ret.length() == init.length()))
          ret.append(" ");
      }

      return ret.toString();
    }

    public MusicDataSearcher<?,?,?,?> getProvider() {
      if (this == SPOTIFY) return new Spotify();
      if (this == YOUTUBE) return new Youtube();
      else return new Spotify(); // <- default
    }

    @Override
    public String toString() {
      return toCamelCase(super.name());
    }
  }

}
