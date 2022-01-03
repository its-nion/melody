package com.lopl.melody.slash;


import com.lopl.melody.commands.essentials.Help;
import com.lopl.melody.commands.essentials.Info;
import com.lopl.melody.commands.essentials.Ping;
import com.lopl.melody.commands.essentials.Settings;
import com.lopl.melody.commands.music.*;
import com.lopl.melody.commands.music.Queue;
import com.lopl.melody.commands.perms.EditPerms;
import com.lopl.melody.commands.perms.ShowPerms;

import java.util.*;

public class SlashCommands
{
    private static final Collection<SlashCommand> commands = List.of(
        new Ping(),
        new Info(),
        new Help(),
        new Settings(),

        new Play(),
        new Player(),
        new Join(),
        new Disconnect(),
        new Pause(),
        new Resume(),
        new Stop(),
        new Skip(),
        new Volume(),
        new Queue(),

        new ShowPerms(),
        new EditPerms()

    );

    public static Collection<SlashCommand> getCommands(){
        return commands;
    }

}
