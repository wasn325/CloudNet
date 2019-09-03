/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.server.info;

import de.dytanic.cloudnet.lib.service.ServiceId;

public class SimpleServerInfo {

    private final ServiceId serviceId;

    private final String hostAddress;

    private final int port;

    private final int onlineCount;

    private final int maxPlayers;

    public SimpleServerInfo(final ServiceId serviceId,
                            final String hostAddress,
                            final int port,
                            final int onlineCount,
                            final int maxPlayers) {
        this.serviceId = serviceId;
        this.hostAddress = hostAddress;
        this.port = port;
        this.onlineCount = onlineCount;
        this.maxPlayers = maxPlayers;
    }

    public int getPort() {
        return port;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public int getOnlineCount() {
        return onlineCount;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getHostAddress() {
        return hostAddress;
    }
}
