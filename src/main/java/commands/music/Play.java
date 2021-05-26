package commands.music;

import audioCore.AudioStateChecks;
import audioCore.PlayerManager;
import commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.CommandHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.awt.*;
import java.net.URISyntaxException;

import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class Play implements Command {
    private CommandHook commandHook;

    private boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    @Override
    public CommandData commandInfo() {
        return new CommandData("play", "Adds a song to your queue")
                .addOption(new OptionData(STRING, "song", "The song name or Url")
                        .setRequired(true));
    }

    @Override
    public void called(SlashCommandEvent event) {

        if (AudioStateChecks.isMemberInVC(event) == false) {
            event.reply(new EmbedBuilder()
                    .setColor(new Color(248, 78, 106, 255))
                    .setDescription("This Command requires **you** to be **connected to a voice channel**")
                    .build())
                    .queue();

            return;
        }

        if (AudioStateChecks.isMelodyInVC(event)) {
            if (AudioStateChecks.isMemberAndMelodyInSameVC(event) == false) {
                event.reply(new EmbedBuilder()
                        .setColor(new Color(248, 78, 106, 255))
                        .setDescription("This Command requires **you** to be **in the same voice channel as Melody**")
                        .build())
                        .queue();

                return;
            }
        }

        this.action(event);
    }

    @Override
    public void action(SlashCommandEvent event) {
        String link = event.getOption("song").getAsString();

        if (isUrl(link) == false)
        {
            link = "ytsearch:" + link;
        }

        event.acknowledge().queue();

        commandHook = event.getHook();

        if(AudioStateChecks.isMelodyInVC(event) == false)
        {
            AudioManager audioManager = event.getGuild().getAudioManager();
            VoiceChannel memberChannel = event.getMember().getVoiceState().getChannel();

            audioManager.openAudioConnection(memberChannel);

            commandHook.sendMessage(new EmbedBuilder()
                    .setColor(new Color(116, 196, 118, 255))
                    .setDescription("**Joined** in <#" + memberChannel.getId() + ">")
                    .build())
                    .queue();
        }

        PlayerManager.getInstance()
                .loadAndPlay(event, link, commandHook);

        return;
    }
}
