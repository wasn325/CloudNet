/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.server.process;

import de.dytanic.cloudnet.lib.Value;
import de.dytanic.cloudnet.lib.interfaces.Executable;
import de.dytanic.cloudnetwrapper.screen.Screenable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ServerDispatcher extends Executable, Screenable {

    Value<Boolean> startup = new Value<>(false);

    static Value<Boolean> getStartup() {
        return startup;
    }

    default void executeCommand(String consoleCommand) {
        if (getInstance() == null && !getInstance().isAlive()) {
            return;
        }

        try {
            final OutputStream outputStream = getInstance().getOutputStream();
            outputStream.write((consoleCommand + '\n').getBytes());
            outputStream.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    Process getInstance();

    default boolean isAlive() {
        try {
            if (getInstance() == null || !getInstance().isAlive()) {
                return false;
            }
            try (InputStream inputStream = getInstance().getInputStream()) {
                return inputStream.available() != -1;
            }
        } catch (IOException e) {
            return false;
        }
    }

}
