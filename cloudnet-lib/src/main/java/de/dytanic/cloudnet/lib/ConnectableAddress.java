package de.dytanic.cloudnet.lib;

/**
 * Created by Tareko on 07.06.2017.
 */
public class ConnectableAddress {

    private final String hostName;
    private final int port;

    public ConnectableAddress(final String hostName, final int port) {
        this.hostName = hostName;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getHostName() {
        return hostName;
    }
}
