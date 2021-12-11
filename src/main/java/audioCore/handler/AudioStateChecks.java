package audioCore.handler;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class AudioStateChecks
{
    public static boolean isMelodyInVC(SlashCommandEvent event)
    {
        return event.getGuild().getSelfMember().getVoiceState().inVoiceChannel();
    }

    public static boolean isMemberInVC(SlashCommandEvent event)
    {
        return event.getMember().getVoiceState().inVoiceChannel();
    }

    public static boolean isMemberAndMelodyInSameVC(SlashCommandEvent event)
    {
        return event.getMember().getVoiceState().getChannel().equals(event.getGuild().getSelfMember().getVoiceState().getChannel());
    }
}
