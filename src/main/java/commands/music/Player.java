package commands.music;

import audioCore.handler.AudioStateChecks;
import audioCore.slash.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;

public class Player extends SlashCommand {

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

    }

    @Override
    protected void clicked(ButtonClickEvent event) {

    }
}
