/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.command;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import jline.console.completer.Completer;

import java.util.*;

/**
 * Class that manages commands for the interfaces of CloudNet.
 */
public final class CommandManager implements Completer {

    private static final String[] EMPTY_ARGS = new String[0];
    private final Map<String, Command> commands = NetworkUtils.newConcurrentHashMap();
    private final ConsoleCommandSender consoleSender = new ConsoleCommandSender();

    /**
     * Constructs a new command manager with a {@link ConsoleCommandSender} and
     * no commands.
     */
    public CommandManager() {
    }

    /**
     * Clears all the commands that are currently registered.
     *
     * @return the command manager this was called on, allows for chaining
     */
    public CommandManager clearCommands() {
        commands.clear();
        return this;
    }

    /**
     * Register a new command and all of its aliases to this command manager.
     *
     * @param command the command to register
     *
     * @return the command manager this was called on, allows for chaining
     */
    public CommandManager registerCommand(final Command command) {
        if (command == null) {
            return this;
        }

        this.commands.put(command.getName().toLowerCase(), command);

        if (command.getAliases().length != 0) {
            for (final String aliases : command.getAliases()) {
                commands.put(aliases.toLowerCase(), command);
            }
        }

        return this;
    }

    /**
     * Get the registered commands.
     *
     * @return a set containing all the registered command names and aliases
     */
    public Set<String> getCommands() {
        return commands.keySet();
    }

    public ConsoleCommandSender getConsoleSender() {
        return consoleSender;
    }

    /**
     * Parses the given {@code command} from the console and dispatches it using
     * a {@link ConsoleCommandSender}.
     *
     * <ol>
     * <li>First all arguments get processed by the {@link CommandArgument} handlers.</li>
     * <li>Then the {@link Command} is executed with the processed commands</li>
     * <li>Last all arguments are processed again</li>
     * </ol>
     *
     * @param command the command line to parse and dispatch
     *
     * @return whether the command executed successfully
     *
     * @see CommandManager#dispatchCommand(CommandSender, String)
     */
    public boolean dispatchCommand(final String command) {
        return dispatchCommand(consoleSender, command);
    }

    /**
     * Parses the given {@code command} and dispatches it using the
     * given {@code sender}.
     *
     * <ol>
     * <li>First all arguments get processed by the {@link CommandArgument} handlers.</li>
     * <li>Then the {@link Command} is executed with the processed commands</li>
     * <li>Last all arguments are processed again</li>
     * </ol>
     *
     * @param sender  the sender to execute the command as
     * @param command the command line to parse and dispatch
     *
     * @return whether the command executed successfully
     */
    public boolean dispatchCommand(final CommandSender sender, final String command) {
        final String[] a = command.split(" ");
        if (this.commands.containsKey(a[0].toLowerCase())) {
            final String b = command.replace((command.contains(" ") ? command.split(" ")[0] + ' ' : command), NetworkUtils.EMPTY_STRING);
            try {
                for (final String argument : a) {
                    for (final CommandArgument commandArgument : this.commands.get(a[0].toLowerCase()).getCommandArguments()) {
                        if (commandArgument.getName().equalsIgnoreCase(argument)) {
                            commandArgument.preExecute(this.commands.get(a[0]), command);
                        }
                    }
                }

                if (b.equals(NetworkUtils.EMPTY_STRING)) {
                    this.commands.get(a[0].toLowerCase()).onExecuteCommand(sender, EMPTY_ARGS);
                } else {
                    final String[] c = b.split(" ");
                    this.commands.get(a[0].toLowerCase()).onExecuteCommand(sender, c);
                }

                for (final String argument : a) {
                    for (final CommandArgument commandArgument : this.commands.get(a[0].toLowerCase()).getCommandArguments()) {
                        if (commandArgument.getName().equalsIgnoreCase(argument)) {
                            commandArgument.postExecute(this.commands.get(a[0]), command);
                        }
                    }
                }

            } catch (final Exception ex) {
                ex.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
        final String[] input = buffer.split(" ");

        final List<String> responses = new ArrayList<>();

        if (buffer.isEmpty() || buffer.indexOf(' ') == -1) {
            responses.addAll(this.commands.keySet());
        } else {
            final Command command = getCommand(input[0]);

            if (command instanceof TabCompletable) {
                final String[] args = buffer.split(" ");
                final String testString = args[args.length - 1];

                responses.addAll(CollectionWrapper.filterMany(((TabCompletable) command).onTab(input.length - 1, input[input.length - 1]),
                    new Acceptable<String>() {
                        @Override
                        public boolean isAccepted(final String s) {
                            return s != null && (testString.isEmpty() || s.toLowerCase().contains(
                                testString.toLowerCase()));
                        }
                    }));
            }
        }

        Collections.sort(responses);

        candidates.addAll(responses);
        final int lastSpace = buffer.lastIndexOf(' ');

        return (lastSpace == -1) ? cursor - buffer.length() : cursor - (buffer.length() - lastSpace - 1);
    }

    /**
     * Get the command for a given name.
     *
     * @param name the name to get the command for
     *
     * @return the command, if there is one with the given {@code name} or alias
     * or {@code null}, if no command matches the {@code name}
     */
    public Command getCommand(final String name) {
        return commands.get(name.toLowerCase());
    }
}
