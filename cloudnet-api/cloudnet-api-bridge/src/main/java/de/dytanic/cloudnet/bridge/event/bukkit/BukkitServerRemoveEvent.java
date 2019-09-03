/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import org.bukkit.event.HandlerList;

/**
 * Calls if a server was removed from the network
 */
public class BukkitServerRemoveEvent extends BukkitCloudEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final ServerInfo serverInfo;

    public BukkitServerRemoveEvent(final ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
