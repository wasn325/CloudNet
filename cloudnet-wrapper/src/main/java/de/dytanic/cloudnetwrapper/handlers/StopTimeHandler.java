/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.handlers;

import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.server.BungeeCord;
import de.dytanic.cloudnetwrapper.server.CloudGameServer;
import de.dytanic.cloudnetwrapper.server.GameServer;

public final class StopTimeHandler implements IWrapperHandler {

    @Override
    public void run(final CloudNetWrapper wrapper) {
        for (final GameServer gameServer : CloudNetWrapper.getInstance().getServers().values()) {
            try {
                if (!gameServer.isAlive()) {
                    if (System.currentTimeMillis() > (gameServer.getStartupTimeStamp() + 1600)) {
                        gameServer.shutdown();
                    } else {
                        gameServer.restart();
                    }
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }

        for (final CloudGameServer gameServer : CloudNetWrapper.getInstance().getCloudServers().values()) {
            try {
                if (!gameServer.isAlive()) {
                    gameServer.shutdown();
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }

        for (final BungeeCord bungeeCord : CloudNetWrapper.getInstance().getProxys().values()) {
            try {
                if (!bungeeCord.isAlive()) {
                    bungeeCord.shutdown();
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    @Override
    public int getTicks() {
        return 10;
    }
}
