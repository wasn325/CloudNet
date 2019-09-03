package de.dytanic.cloudnetcore.command;
/*
 * Created by derrop on 04.06.2019
 */

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

public class CommandLocalWrapper extends Command {
    public CommandLocalWrapper() {
        super("local-wrapper", "cloudnet.command.local-wrapper", "lw");

        description = "Manages the local wrapper in the master of this cloud if it is enabled";
    }

    @Override
    public void onExecuteCommand(final CommandSender sender, final String[] args) {
        if (!CloudNet.getInstance().getLocalCloudWrapper().isEnabled()) {
            sender.sendMessage("The local wrapper is disabled, enable it by adding \"--installWrapper\" to the end of your start command");
            return;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
            final Wrapper wrapper = CloudNet.getInstance().getLocalCloudWrapper().getWrapper();
            if (wrapper == null) {
                sender.sendMessage("Wrapper not found!");
                return;
            }

            sender.sendMessage("Local wrapper " + wrapper.getName() + ':',
                               "Info: CPU Usage: " + NetworkUtils.DECIMAL_FORMAT.format(wrapper.getCpuUsage()) + "/100% | Memory: " +
                               wrapper.getUsedMemory() + NetworkUtils.SLASH_STRING + wrapper.getMaxMemory() + "MB",
                               " ");

            final Configuration configuration = CloudNet.getInstance().getLocalCloudWrapper().loadWrapperConfiguration();
            try (final StringWriter writer = new StringWriter()) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, writer);
                sender.sendMessage("Config:");
                sender.sendMessage(writer.toString().split("\n"));
            } catch (final IOException e) {
                e.printStackTrace();
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("restart")) {
            try {
                CloudNet.getInstance().getLocalCloudWrapper().restart();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("console")) {
            final boolean preEnabled = CloudNet.getInstance().getLocalCloudWrapper().isShowConsoleOutput();
            CloudNet.getInstance().getLocalCloudWrapper().setShowConsoleOutput(!preEnabled);
            sender.sendMessage("Logging of the local wrapper has been " + (preEnabled ? "disabled" : "enabled"));
        } else if (args.length >= 2 && args[0].equalsIgnoreCase("cmd")) {
            final String command = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            if (command.trim().isEmpty()) {
                sender.sendMessage("Cannot send empty command");
                return;
            }
            sender.sendMessage("Sending command to local wrapper...");
            try {
                CloudNet.getInstance().getLocalCloudWrapper().executeCommand(command);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        } else {
            sender.sendMessage("lw info", "lw restart", "lw cmd", "lw console");
        }
    }
}
