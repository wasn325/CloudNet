/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;

public class CommandHelp extends Command {

    public CommandHelp() {
        super("help", "cloudnet.command.help");
    }

    @Override
    public void onExecuteCommand(final CommandSender sender, final String[] args) {
        sender.sendMessage("You can use the commands \"reload\", \"clear\", \"stop\", \"version\" and \"clearcache\"");
    }
}
