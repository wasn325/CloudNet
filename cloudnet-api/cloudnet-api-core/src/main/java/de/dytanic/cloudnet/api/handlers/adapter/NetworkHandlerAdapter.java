/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.handlers.adapter;

import de.dytanic.cloudnet.api.handlers.NetworkHandler;
import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.UUID;

/**
 * Simpled Adapter for the network server.
 * You can extends this Class for use some methods
 */
public class NetworkHandlerAdapter implements NetworkHandler {

    @Override
    public void onServerAdd(final ServerInfo serverInfo) {

    }

    @Override
    public void onServerInfoUpdate(final ServerInfo serverInfo) {

    }

    @Override
    public void onServerRemove(final ServerInfo serverInfo) {

    }

    @Override
    public void onProxyAdd(final ProxyInfo proxyInfo) {

    }

    @Override
    public void onProxyInfoUpdate(final ProxyInfo proxyInfo) {

    }

    @Override
    public void onProxyRemove(final ProxyInfo proxyInfo) {

    }

    @Override
    public void onCloudNetworkUpdate(final CloudNetwork cloudNetwork) {

    }

    @Override
    public void onCustomChannelMessageReceive(final String channel, final String message, final Document document) {

    }

    @Override
    public void onCustomSubChannelMessageReceive(final String channel, final String message, final Document document) {

    }

    @Override
    public void onPlayerLoginNetwork(final CloudPlayer cloudPlayer) {

    }

    @Override
    public void onPlayerDisconnectNetwork(final CloudPlayer cloudPlayer) {

    }

    @Override
    public void onPlayerDisconnectNetwork(final UUID uniqueId) {

    }

    @Override
    public void onPlayerUpdate(final CloudPlayer cloudPlayer) {

    }

    @Override
    public void onOfflinePlayerUpdate(final OfflinePlayer offlinePlayer) {

    }

    @Override
    public void onUpdateOnlineCount(final int onlineCount) {

    }
}
