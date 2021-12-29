package audioCore.provider;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;

public interface MusicDataSearcher {

  void search(@NotNull SlashCommandEvent event, @NotNull String search, Message message);

}
