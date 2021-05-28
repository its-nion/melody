package commands.music;

import commands.Command;
import database.DBVariables;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;

import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

public class Volume implements Command
{
    @Override
    public CommandData commandInfo() {
        return new CommandData("volume", "Check or change your current volume")
                .addOption(new OptionData(INTEGER, "amount", "Volume in percentage [0 - 100]")
                        .setRequired(false));
    }

    @Override
    public void called(SlashCommandEvent event)
    {
        final Member self = event.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!event.getMember().getVoiceState().inVoiceChannel()) // If member is not inside voice channel
        {
            event.replyEmbeds(new EmbedBuilder()
                    .setColor(new Color(248,78,106,255))
                    .setDescription("This Command requires **you** to be **connected to a voice channel**")
                    .build())
                    .queue();

            return;
        }

        if(!selfVoiceState.inVoiceChannel()) // If bot is not in voice channel
        {
            event.replyEmbeds(new EmbedBuilder()
                    .setColor(new Color(248,78,106,255))
                    .setDescription("This Command requires Melody to be **connected to your voice channel**")
                    .build())
                    .queue();

            return;
        }

        if(selfVoiceState.inVoiceChannel() && !memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) // Checks if user is in same vc as Bot
        {
            event.replyEmbeds(new EmbedBuilder()
                    .setColor(new Color(248,78,106,255))
                    .setDescription("This Command requires **you** to be **in the same voice channel as Melody**")
                    .build())
                    .queue();

            return;
        }

        this.action(event);
    }

    @Override
    public void action(SlashCommandEvent event) {
        final OptionMapping option = event.getOption("amount");

        if(option != null)
        {
            final long vol = event.getOption("amount").getAsLong();

            if (vol <= 100 && vol >= 0)
            {
                DBVariables.setVolume(event.getGuild().getIdLong(), (int)vol);

                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("Volume was set to **" + vol + "%**")
                        .build())
                        .queue();

                return;
            }
            else
            {
                event.replyEmbeds(new EmbedBuilder()
                        .setColor(new Color(248,78,106,255))
                        .setDescription("Type in a number between **0 and 100**")
                        .build())
                        .queue();

                return;
            }
        }
        else
        {
            final int vol = DBVariables.getVolume(event.getGuild().getIdLong());

            event.replyEmbeds(new EmbedBuilder()
                    .setTitle( vol + "%")
                    .build())
                    .queue();
            return;
        }
    }
}
