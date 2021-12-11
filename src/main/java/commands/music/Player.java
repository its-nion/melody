package commands.music;

import audioCore.handler.AudioStateChecks;
import audioCore.handler.PlayerManager;
import audioCore.handler.TrackScheduler;
import audioCore.slash.SlashCommand;
import com.jagrosh.jdautilities.command.Command;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import utils.ReactionEmoji;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Player extends SlashCommand {

    public static final String PlayPause = "play_pause";
    public static final String SkipForward = "skip";
    public static final String SkipBackwards = "skip_reverse";
    public static final String Stop = "stop";

    public Player() {
        super.name = "controls";
        super.category = new Command.Category("Sound");
        super.arguments = "";
        super.help = "/player : Shows some clickable buttons to control the best of all music bots. It also has a lot of donuts!";
        super.description = "Shows some clickable buttons to control the best of all music bots. It also has a lot of donuts!";
    }

//    @Override
//    public CommandData commandInfo() {
//        return new CommandData("player", "Opens the player");
//    }
//
//    @Override
//    public void called(SlashCommandEvent event) {
//        if (!AudioStateChecks.isMemberInVC(event)) {
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
//        event.replyEmbeds(new EmbedBuilder()
//                .setDescription("[Song Name](https://www.youtube.com/watch?v=dQw4w9WgXcQ)" + "   4:12")
//                .build())
//        .addActionRow(Button.secondary("0", "⏮"), Button.secondary("0", "▶️"), Button.secondary("0", "⏹"), Button.secondary("0", "⏭"), Button.secondary("0", "\uD83D\uDD00"))//▶⏹
//                .addActionRow(Button.secondary("0", "back"), Button.secondary("0", "pause"), Button.secondary("0", "stop"), Button.secondary("0", "skip"), Button.secondary("0", "shuffle"))
//                .addActionRow(Button.secondary("0", "ᐅ"), Button.secondary("0", "□"), Button.secondary("0", "»"), Button.secondary("0", "↝"))
//        .queue();
//    }

    @Override
    protected void execute(SlashCommandEvent event) {
        assert event.getGuild() != null;
        PlayerManager manager = PlayerManager.getInstance();
        AudioPlayer player = manager.getGuildAudioManager(event.getGuild()).player;
        ActionRow buttons = getActionRow(event.getGuild());
        for (net.dv8tion.jda.api.interactions.components.Button button : buttons.getButtons()) registerButton(button);

        event.replyEmbeds(getSongEmbed(player.getPlayingTrack()))
            .addActionRow(buttons.getButtons())
            .queue();
    }

    public ActionRow getActionRow(Guild guild){
        PlayerManager manager = PlayerManager.getInstance();
        AudioPlayer player = manager.getGuildAudioManager(guild).player;
        TrackScheduler scheduler = manager.getGuildAudioManager(guild).scheduler;
        boolean isPlaying = !player.isPaused() && player.getPlayingTrack() != null;
        boolean hasTrack = player.getPlayingTrack() != null;
        boolean hasNextTrack = !scheduler.getQueue().isEmpty();

        net.dv8tion.jda.api.interactions.components.Button bPlayPause = net.dv8tion.jda.api.interactions.components.Button.success(PlayPause, Emoji.fromUnicode(isPlaying ? ReactionEmoji.PLAY : ReactionEmoji.PAUSE));
        if (!hasTrack) bPlayPause = net.dv8tion.jda.api.interactions.components.Button.danger(PlayPause, Emoji.fromUnicode(ReactionEmoji.PAUSE));

        net.dv8tion.jda.api.interactions.components.Button bSkip = net.dv8tion.jda.api.interactions.components.Button.primary(SkipForward, Emoji.fromUnicode(ReactionEmoji.NEXT));
        if (!hasNextTrack) bSkip = net.dv8tion.jda.api.interactions.components.Button.danger(SkipForward, Emoji.fromUnicode(ReactionEmoji.NEXT));

        net.dv8tion.jda.api.interactions.components.Button bPrevious = net.dv8tion.jda.api.interactions.components.Button.danger(SkipBackwards, Emoji.fromUnicode(ReactionEmoji.PREVIOUS));

        net.dv8tion.jda.api.interactions.components.Button bStop = net.dv8tion.jda.api.interactions.components.Button.primary(Stop, Emoji.fromUnicode(ReactionEmoji.STOP));
        if (!hasTrack) bStop = Button.danger(Stop, Emoji.fromUnicode(ReactionEmoji.STOP));


        return ActionRow.of(bPrevious, bPlayPause, bSkip, bStop);
    }

    public MessageEmbed getSongEmbed(AudioTrack audioTrack){
        if (audioTrack == null)
            return new EmbedBuilder().setDescription("No track currently playing.").build();

        AudioTrackInfo trackInfo = audioTrack.getInfo();
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(Color.CYAN)
            .setTitle(trackInfo.title)
            .setAuthor(trackInfo.author)
            .setThumbnail("https://raw.githubusercontent.com/Phil0L/DonutMusic/master/imgs/Donut-Bot-Playing.png")
            .setDescription(
                new SimpleDateFormat("mm:ss").format(new Date(audioTrack.getPosition())) + " / " +
                    new SimpleDateFormat("mm:ss").format(new Date(trackInfo.length)) + "\n");
        return eb.build();
    }

    @Override
    protected void clicked(ButtonClickEvent event) {
        assert event.getGuild() != null;
        assert event.getButton() != null;
        assert event.getButton().getId() != null;
        assert event.getButton().getEmoji() != null;
        switch (event.getButton().getId()){
            case PlayPause:
                boolean isPlayEmoji = event.getButton().getEmoji().getName().equals(ReactionEmoji.PLAY);
                boolean isPauseEmoji = event.getButton().getEmoji().getName().equals(ReactionEmoji.PAUSE);
                if (isPlayEmoji){
                    new Resume().resume(event.getGuild());
                    event.editButton(event.getButton().withEmoji(Emoji.fromUnicode(ReactionEmoji.PAUSE))).queue();
                }
                if (isPauseEmoji){
                    new Pause().pause(event.getGuild());
                    event.editButton(event.getButton().withEmoji(Emoji.fromUnicode(ReactionEmoji.PAUSE))).queue();
                }
                break;
            case SkipForward:
                new Skip().skip(event.getGuild());
                break;
            case SkipBackwards:
                break;
            case Stop:
                new Stop().stop(event.getGuild());
                break;
            default:
                break;
        }
    }
}
