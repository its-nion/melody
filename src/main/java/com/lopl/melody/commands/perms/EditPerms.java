package com.lopl.melody.commands.perms;

import com.lopl.melody.audioCore.slash.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class EditPerms extends SlashCommand {


    @Override
    protected void execute(SlashCommandEvent event) {

    }

//    @Override
//    public CommandData commandInfo() {
//        return new CommandData("editperms", "Edit role permissions")
//                .addOption(new OptionData(ROLE, "role", "The role you want to edit permissions on")
//                        .setRequired(true))
//
//                .addOption(new OptionData(STRING, "action", "What you want to do with the permission")
//                        .setRequired(true)
//                .addChoice("allow", "allow")
//                .addChoice("deny", "deny")
//                .addChoice("clear", "clear"))
//
//                .addOption(new OptionData(STRING, "permission", "Which permission you want to edit")
//                .setRequired(true)
//                .addChoice("play", "play"));
//    }


}
