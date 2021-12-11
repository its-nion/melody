package commands.music;


import audioCore.handler.MusicLoader;
import audioCore.slash.SlashCommand;
import audioCore.spotify.Spotify;
import audioCore.spotify.SpotifyButtonMessage;
import com.jagrosh.jdautilities.command.Command;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import melody.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import utils.MessageStore;
import utils.ReactionEmoji;
import utils.SavedMessage;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Play extends SlashCommand {

    public static Player player;

    public static final String URL_TYPE_SEARCH = "search";
    public static final String NEXT = "next";
    public static final String PREVIOUS = "previous";
    public static final String PLAY = "play";

    public Play() {
        super.name = "play";
        super.category = new Command.Category("Sound");
        super.help = """
        /play [link] : plays the song/playlist of the link. possible links from youtube, soundcloud or bandcamp
        /play playlist [search] : searches for a playlist with spotify. react with eiter the "arrow left" or the "arrow right" to navigate threw the results, react with "musical_note" to play the playlist or react with "+" to queue the playlist. aliases: pl, list
        /play song [search] : searches for a song with spotify. react with eiter the "arrow left" or the "arrow right" to navigate threw the results, react with "musical_note" to play the song or react with "+" to queue the song. aliases: track
        /play user [userID] : lists the users playlists on spotify. react with eiter the "arrow left" or the "arrow right" to navigate threw the playlists, react with "musical_note" to play the playlist or react with "+" to queue the playlist. aliases: account, member
        /play [search] : searches for a song""";
        super.description = "Search for your favourite songs to jam to.";
    }

//    private InteractionHook interactionHook;
//
//    private boolean isUrl(String url) {
//        try {
//            new URI(url);
//            return true;
//        } catch (URISyntaxException e) {
//            return false;
//        }
//    }
//
//    @Override
//    public CommandData commandInfo() {
//        return new CommandData("play", "Adds a song to your queue")
//                .addOption(new OptionData(STRING, "song", "The song name or Url")
//                        .setRequired(true));
//    }
//
//    @Override
//    public void called(SlashCommandEvent event) {
//
//        if (AudioStateChecks.isMemberInVC(event) == false) {
//            event.replyEmbeds(new EmbedBuilder()
//                    .setColor(new Color(248, 78, 106, 255))
//                    .setDescription("This Command requires **you** to be **connected to a voice channel**")
//                    .build())
//                    .queue();
//
//            return;
//        }
//
//        if (AudioStateChecks.isMelodyInVC(event)) {
//            if (AudioStateChecks.isMemberAndMelodyInSameVC(event) == false) {
//                event.replyEmbeds(new EmbedBuilder()
//                        .setColor(new Color(248, 78, 106, 255))
//                        .setDescription("This Command requires **you** to be **in the same voice channel as Melody**")
//                        .build())
//                        .queue();
//
//                return;
//            }
//        }
//
//        this.action(event);
//    }
//
//    @Override
//    public void action(SlashCommandEvent event) {
//        String link = event.getOption("song").getAsString();
//
//        if (isUrl(link) == false)
//        {
//            link = "ytsearch:" + link;
//        }
//
//        //event.isAcknowledged() = true;
//
//        interactionHook = event.getHook();
//
//        if(AudioStateChecks.isMelodyInVC(event) == false)
//        {
//            AudioManager audioManager = event.getGuild().getAudioManager();
//            VoiceChannel memberChannel = event.getMember().getVoiceState().getChannel();
//
//            audioManager.openAudioConnection(memberChannel);
//
//            interactionHook.sendMessageEmbeds(new EmbedBuilder()
//                    .setColor(new Color(116, 196, 118, 255))
//                    .setDescription("**Joined** in <#" + memberChannel.getId() + ">")
//                    .build())
//                    .queue();
//        }
//
//        PlayerManager.getInstance()
//                .loadAndPlay(event, link, interactionHook);
//
//        return;
//    }

    @Override
    protected CommandCreateAction onUpsert(CommandCreateAction cca) {
        return cca.addOption(OptionType.STRING, URL_TYPE_SEARCH, "URL or Type or Search", true);
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        //Main.log(event, "Play");
        TextChannel channel = event.getTextChannel();

        if (event.getOptions().isEmpty()) {
            channel.sendMessage("Please provide some arguments, type %help play for further information").queue();
        } else {
            Player player = getPlayer();
            //Main.info(event, "Player loaded: " + player.name(), Main.ANSI_GREEN);
            Play.player = player;
            handlePlayCommand(event, player);

        }
    }

    public void handlePlayCommand(SlashCommandEvent event, Player player) {
        assert event.getGuild() != null;
        AudioManager audio = event.getGuild().getAudioManager();
        MessageEmbed connect = null;
        if (!audio.isConnected())
            connect = new Join().connect(event);

        OptionMapping firstOption = event.getOption(URL_TYPE_SEARCH);
        assert firstOption != null;
        String firstArg = firstOption.getAsString();

        if (isUrl(firstArg)) {
            // URL
            //Main.info(event, "Loading song or playlist " + firstArg, Main.ANSI_BLUE);
            new MusicLoader().loadURL(event.getTextChannel(), firstArg.strip());
        } else {
            // create buttons
            net.dv8tion.jda.api.interactions.components.Button[] buttons = new net.dv8tion.jda.api.interactions.components.Button[]{
                net.dv8tion.jda.api.interactions.components.Button.danger(PREVIOUS, Emoji.fromUnicode(ReactionEmoji.PREVIOUS)),
                net.dv8tion.jda.api.interactions.components.Button.secondary(PLAY, Emoji.fromUnicode(ReactionEmoji.PLAY)),
                net.dv8tion.jda.api.interactions.components.Button.secondary(NEXT, Emoji.fromUnicode(ReactionEmoji.NEXT))
            };
            for (net.dv8tion.jda.api.interactions.components.Button component : buttons)
                registerButton(component);

            // create embeds
            MessageEmbed first = connect != null ? connect : new EmbedBuilder().setDescription("Loading Spotify...").build();
            MessageEmbed second = connect == null ? null : new EmbedBuilder().setDescription("Loading Spotify...").build();
            List<MessageEmbed> embeds = newArrayList(first, second);
            embeds.remove(null);

            // create Message from embeds and buttons
            Message message = event
                .replyEmbeds(embeds)
                .addActionRow(buttons).complete().retrieveOriginal().complete();

            //Main.info(event, "Searching for songs: '" + firstArg + "' with: " + player.name(), Main.ANSI_BLUE);
            Spotify.searchSpotify(event, firstArg, message);
        }
    }

    private List<MessageEmbed> newArrayList(MessageEmbed... embeds) {
        ArrayList<MessageEmbed> newList = new ArrayList<>();
        for (MessageEmbed embed : embeds) if (embed != null) newList.add(embed);
        return newList;
    }

    private boolean isUrl(String input) {
        try {
            new URL(input);
            return true;
        } catch (MalformedURLException ignored) {
            return false;
        }
    }

    @Override
    protected void clicked(ButtonClickEvent event) {
        assert event.getButton() != null;
        assert event.getButton().getId() != null;
        for (SavedMessage message : MessageStore.allMessages()) {
            if (message.getMessageID() == event.getMessageIdLong() && message instanceof SpotifyButtonMessage) {
                foundSpotifyMessage(event, (SpotifyButtonMessage) message);
                if (!event.isAcknowledged())
                    event.deferEdit().queue();
                return;
            }
        }

    }

    private void foundSpotifyMessage(@Nonnull ButtonClickEvent event, SpotifyButtonMessage spotifyMessage) {
        assert event.getButton() != null;
        assert event.getButton().getId() != null;
        switch (event.getButton().getId()){
            case PREVIOUS:
                if (spotifyMessage.hasPrevious()) {
                    spotifyMessage.index--;
                    spotifyMessage.show();
                }
                break;
            case PLAY:
                queue(event, spotifyMessage);
                break;
            case NEXT:
                if (spotifyMessage.hasNext()) {
                    spotifyMessage.index++;
                    spotifyMessage.show();
                }
                break;
            default:
                break;
        }
        event.editComponents(ActionRow.of(
            net.dv8tion.jda.api.interactions.components.Button.of(spotifyMessage.hasPrevious() ? ButtonStyle.SECONDARY : ButtonStyle.DANGER, PREVIOUS, Emoji.fromUnicode(ReactionEmoji.PREVIOUS)),
            net.dv8tion.jda.api.interactions.components.Button.secondary(PLAY, Emoji.fromUnicode(ReactionEmoji.PLAY)),
            Button.of(spotifyMessage.hasNext() ? ButtonStyle.SECONDARY : ButtonStyle.DANGER, NEXT, Emoji.fromUnicode(ReactionEmoji.NEXT))
        )).queue();
    }

    private void queue(ButtonClickEvent event, SpotifyButtonMessage spotifyMessage) {
        assert event.getGuild() != null;
        new Join().connect(event.getGuild(), event.getMember(), event.getTextChannel());

        if (spotifyMessage.tracks == null) {
            //PLAYLIST
            List<String> queries = new ArrayList<>();
            PlaylistTrack[] tracks = Spotify.getPlaylistsTracks(spotifyMessage.getCurrentPlaylist().getId());
            int maxLoadCount = 100;
            for (int i = 0; i < tracks.length && i < maxLoadCount; i++) {
                String artist = tracks[i].getTrack().getArtists()[0].getName();
                String title = tracks[i].getTrack().getName();
                String search = "ytsearch:" + artist + "-" + title;
                queries.add(search);
            }
            Main.info(event, "Finding " + queries.size() + " Tracks on Youtube");
            new MusicLoader().loadMultiple(event.getTextChannel(), spotifyMessage.getCurrentPlaylist(), queries.toArray(String[]::new));
        } else {
            //SINGLE
            Track track = spotifyMessage.getCurrentTrack();
            String url = "ytsearch:" + track.getArtists()[0].getName() + "-" + track.getName();
            Main.info(event, "Finding Track on Youtube with: " + url);
            new MusicLoader().loadOne(event.getTextChannel(), url);
        }
    }

    enum Player {
        NONE, YOUTUBE, SPOTIFY
    }

    private Player getPlayer() {
        if (player != Player.NONE && player != null) {
            return player;
        }
        // unknown player -> return default
        player = Player.SPOTIFY;
        return player;
    }
}
