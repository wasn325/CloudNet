package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;

/**
 * Created by Tareko on 19.01.2018.
 */
public final class CommandVersion extends Command {

    public CommandVersion() {
        super("version", "cloudnet.command.version");

        description = "Shows the version of this instance";

    }

    @Override
    public void onExecuteCommand(final CommandSender sender, final String[] args) {
        sender.sendMessage("CloudNet " + NetworkUtils.class.getPackage().getSpecificationVersion() + " #" + NetworkUtils.class.getPackage()
                                                                                                                              .getImplementationVersion() + " by Dytanic");
    }
}
