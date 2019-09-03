/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.modules.Module;
import de.dytanic.cloudnetcore.CloudNet;

/**
 * Created by Tareko on 23.08.2017.
 */
public final class CommandModules extends Command {

    public CommandModules() {
        super("modules", "cloudnet.command.modules", "m");

        description = "Lists all modules, versions and authors";

    }

    @Override
    public void onExecuteCommand(final CommandSender sender, final String[] args) {
        sender.sendMessage("Running modules:", NetworkUtils.SPACE_STRING);
        for (final Module module : CloudNet.getInstance().getModuleManager().getModules()) {
            sender.sendMessage(module.getName() + ' ' + module.getModuleConfig().getVersion() + " by " + module.getModuleConfig()
                                                                                                               .getAuthor() + NetworkUtils.EMPTY_STRING);
        }
    }
}
