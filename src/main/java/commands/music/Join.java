package commands.music;

import audioCore.handler.AudioStateChecks;
import audioCore.slash.SlashCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.time.OffsetDateTime;

public class Join extends SlashCommand {


    @Override
    protected void execute(SlashCommandEvent event) {
        if(!AudioStateChecks.isMemberInVC(event))
        {
            event.replyEmbeds(new EmbedBuilder()
                .setColor(new Color(248,78,106,255))
                .setDescription("This command requires you to be **connected to a voice channel**")
                .build())
                .queue();

            return;
        }

        AudioManager audioManager = event.getGuild().getAudioManager();
        VoiceChannel memberChannel = event.getMember().getVoiceState().getChannel();

        if(AudioStateChecks.isMelodyInVC(event))
        {
            audioManager.openAudioConnection(memberChannel);

            event.replyEmbeds(new EmbedBuilder()
                .setColor(new Color(88,199,235,255))
                .setDescription("**Moved** to <#" + memberChannel.getId() + ">")
                .build())
                .queue();
        }
        else
        {
            audioManager.openAudioConnection(memberChannel);

            event.replyEmbeds(new EmbedBuilder()
                .setColor(new Color(116,196,118,255))
                .setDescription("**Joined** in <#" + memberChannel.getId() + ">")
                .build())
                .queue();

        }
    }

    @Override
    protected void clicked(ButtonClickEvent event) {

    }

    public void connect(CommandEvent event){
        AudioManager audio = event.getGuild().getAudioManager();
        if (audio.isConnected())
            return;
        try {
            assert event.getMember().getVoiceState() != null;
            assert event.getMember().getVoiceState().getChannel() != null;
            VoiceChannel channel = event.getMember().getVoiceState().getChannel();
            audio.openAudioConnection(channel);
            sendJoinMessage(channel, event.getTextChannel());
        } catch (Exception e) {
            event.reply("You have to be in a Voicechannel");
        }
    }

    public void connect(GuildMessageReceivedEvent event){
        AudioManager audio = event.getGuild().getAudioManager();
        if (audio.isConnected())
            return;
        try {
            assert event.getMember() != null;
            assert event.getMember().getVoiceState() != null;
            assert event.getMember().getVoiceState().getChannel() != null;
            VoiceChannel channel = event.getMember().getVoiceState().getChannel();
            audio.openAudioConnection(channel);
            sendJoinMessage(channel, event.getChannel());
        } catch (Exception e) {
            event.getChannel().sendMessage("You have to be in a Voicechannel").queue();
        }
    }
    public MessageEmbed connect(SlashCommandEvent event){
        assert event.getGuild() != null;
        AudioManager audio = event.getGuild().getAudioManager();
        if (audio.isConnected())
            return null;
        try {
            assert event.getMember() != null;
            assert event.getMember().getVoiceState() != null;
            assert event.getMember().getVoiceState().getChannel() != null;
            VoiceChannel channel = event.getMember().getVoiceState().getChannel();
            audio.openAudioConnection(channel);
            return getJoinMessageEmbed(channel);
        } catch (Exception e) {
            event.reply("You have to be in a Voicechannel").queue();
            return null;
        }
    }


    public void connect(Guild guild, Member member, TextChannel textChannel){
        AudioManager audio = guild.getAudioManager();
        if (audio.isConnected())
            return;
        try {
            assert member.getVoiceState() != null;
            assert member.getVoiceState().getChannel() != null;
            VoiceChannel channel = member.getVoiceState().getChannel();
            audio.openAudioConnection(channel);
            sendJoinMessage(channel, textChannel);
        } catch (Exception e) {
            textChannel.sendMessage("You have to be in a Voicechannel").queue();
        }
    }

    private void sendJoinMessage(VoiceChannel channel, TextChannel textChannel) {
        textChannel.sendMessageEmbeds(getJoinMessageEmbed(channel)).queue();
    }

    private MessageEmbed getJoinMessageEmbed(VoiceChannel channel){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.ORANGE);
        embed.setTitle("Joined voice channel '" + channel.getName() + "'. Ready to play some music and eat tons of donuts!");
        embed.setTimestamp(OffsetDateTime.now());
        return embed.build();
    }
}
