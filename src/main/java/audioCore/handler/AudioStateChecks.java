package audioCore.handler;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class AudioStateChecks
{
    public static boolean isMelodyInVC(SlashCommandEvent event) {
        return isMelodyInVC(event.getGuild());
    }

    public static boolean isMelodyInVC(ButtonClickEvent event) {
        return isMelodyInVC(event.getGuild());
    }

    public static boolean isMelodyInVC(Guild guild) {
        if (guild == null) return false;
        GuildVoiceState botVS = guild.getSelfMember().getVoiceState();
        if (botVS == null) return false;
        return botVS.inVoiceChannel();
    }

    public static boolean isMemberInVC(SlashCommandEvent event)
    {
        return isMemberInVC(event.getMember());
    }

    public static boolean isMemberInVC(ButtonClickEvent event)
    {
        return isMemberInVC(event.getMember());
    }

    public static boolean isMemberInVC(Member user)
    {
        if (user == null) return false;
        GuildVoiceState userVS = user.getVoiceState();
        if (userVS == null) return false;
        return userVS.inVoiceChannel();
    }

    public static boolean isMemberAndMelodyInSameVC(SlashCommandEvent event) {
        return isMemberAndMelodyInSameVC(event.getGuild(), event.getMember());
    }

    public static boolean isMemberAndMelodyInSameVC(ButtonClickEvent event) {
        return isMemberAndMelodyInSameVC(event.getGuild(), event.getMember());
    }

    public static boolean isMemberAndMelodyInSameVC(Guild guild, Member user) {
        if (guild == null) return false;
        if (user == null) return false;
        GuildVoiceState userVS = user.getVoiceState();
        if (userVS == null) return false;
        GuildVoiceState botVS = guild.getSelfMember().getVoiceState();
        if (botVS == null) return false;
        VoiceChannel userChannel = userVS.getChannel();
        if (userChannel == null) return false;
        VoiceChannel botChannel = botVS.getChannel();
        if (botChannel == null) return false;
        return userChannel.getIdLong() == botChannel.getIdLong();
    }
}
