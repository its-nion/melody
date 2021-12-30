package com.lopl.melody.audioCore.slash;


import com.lopl.melody.commands.essentials.Help;
import com.lopl.melody.commands.essentials.Info;
import com.lopl.melody.commands.essentials.Ping;
import com.lopl.melody.commands.music.*;
import com.lopl.melody.commands.perms.EditPerms;
import com.lopl.melody.commands.perms.ShowPerms;

import java.util.HashMap;
import java.util.Map;

public class SlashCommands
{
    public static HashMap<String, SlashCommand> commandMap = new HashMap<>(Map.ofEntries(
        Map.entry("ping", new Ping()),
        Map.entry("info", new Info()),
        Map.entry("help", new Help()),
        Map.entry("player", new Player()),
        Map.entry("join", new Join()),
        Map.entry("disconnect", new Disconnect()),
        Map.entry("play", new Play()),
        Map.entry("pause", new Pause()),
        Map.entry("resume", new Resume()),
        Map.entry("stop", new Stop()),
        Map.entry("skip", new Skip()),
        Map.entry("volume", new Volume()),
        Map.entry("queue", new Queue()),
        Map.entry("editperms", new EditPerms()),
        Map.entry("showperms", new ShowPerms())
    ));

}
