package de.dytanic.cloudnet.lib.server.info;

import de.dytanic.cloudnet.lib.service.ServiceId;

/**
 * Created by Tareko on 02.07.2017.
 */
public class SimpleProxyInfo {

    private final ServiceId serviceId;
    private final boolean online;
    private final String hostName;
    private final int port;
    private final int memory;
    private final int onlineCount;

    public SimpleProxyInfo(final ServiceId serviceId,
                           final boolean online,
                           final String hostName,
                           final int port,
                           final int memory,
                           final int onlineCount) {
        this.serviceId = serviceId;
        this.online = online;
        this.hostName = hostName;
        this.port = port;
        this.memory = memory;
        this.onlineCount = onlineCount;
    }

    public int getOnlineCount() {
        return onlineCount;
    }

    public int getPort() {
        return port;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public int getMemory() {
        return memory;
    }

    public String getHostName() {
        return hostName;
    }

    public boolean isOnline() {
        return online;
    }
}
