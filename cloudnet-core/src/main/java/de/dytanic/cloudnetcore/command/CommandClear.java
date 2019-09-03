/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnetcore.CloudNet;

import java.io.IOException;

public final class CommandClear extends Command {

    public CommandClear() {
        super("clear", "cloudnet.command.clear");

        description = "Clears the console";

    }

    @Override
    public void onExecuteCommand(final CommandSender sender, final String[] args) {
        try {
            // Can't close the reader, we need it later
            //noinspection resource
            CloudNet.getLogger().getReader().clearScreen();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
