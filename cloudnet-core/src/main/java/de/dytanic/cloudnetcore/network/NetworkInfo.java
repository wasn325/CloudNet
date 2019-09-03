/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network;

/**
 * Created by Tareko on 27.05.2017.
 */
public class NetworkInfo {

    private final String serverId;
    private final String hostName;
    private final int port;

    public NetworkInfo(final String serverId, final String hostName, final int port) {
        this.serverId = serverId;
        this.hostName = hostName;
        this.port = port;
    }

    public String getHostName() {
        return hostName;
    }

    public String getServerId() {
        return serverId;
    }

    public int getPort() {
        return port;
    }
}
