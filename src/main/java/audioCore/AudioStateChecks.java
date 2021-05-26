package audioCore;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class AudioStateChecks
{
    public static boolean isMelodyInVC(SlashCommandEvent event)
    {
        return (event.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) ? true : false;
    }

    public static boolean isMemberInVC(SlashCommandEvent event)
    {
        return (event.getMember().getVoiceState().inVoiceChannel()) ? true : false;
    }

    public static boolean isMemberAndMelodyInSameVC(SlashCommandEvent event)
    {
        return (event.getMember().getVoiceState().getChannel().equals(event.getGuild().getSelfMember().getVoiceState().getChannel())) ? true : false;
    }
}
