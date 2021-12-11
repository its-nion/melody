package commands.essentials;

import audioCore.slash.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;


public class Info extends SlashCommand {

    public Info() {

    }


    @Override
    protected void execute(SlashCommandEvent event) {
        event.replyEmbeds(new EmbedBuilder()
            .setDescription("Hey there! If you have any buggs to report, feel free to contact me")
            .build())
            .addActionRow(Button.link("https://github.com/its-nion/melody", "Github"),
                Button.link("https://discord.com/api/oauth2/authorize?client_id=843" +
                    "104592417390612&permissions=2184202240&scope=bot%20applications.commands", "Invite"))
            .setEphemeral(true)
            .queue();
    }

    @Override
    protected void clicked(ButtonClickEvent event) {

    }
}
