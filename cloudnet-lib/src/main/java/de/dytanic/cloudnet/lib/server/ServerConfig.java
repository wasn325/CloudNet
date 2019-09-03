package de.dytanic.cloudnet.lib.server;

import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 25.07.2017.
 */
public class ServerConfig {

    private final long startup;
    private boolean hideServer;
    private String extra;
    private Document properties;

    public ServerConfig(final boolean hideServer, final String extra, final Document properties, final long startup) {
        this.hideServer = hideServer;
        this.extra = extra;
        this.properties = properties;
        this.startup = startup;
    }

    @Override
    public String toString() {
        return "ServerConfig{" + "hideServer=" + hideServer + ", extra='" + extra + '\'' + ", properties=" + properties + ", startup=" +
               startup + '}';
    }

    public Document getProperties() {
        return properties;
    }

    public void setProperties(final Document properties) {
        this.properties = properties;
    }

    public long getStartup() {
        return startup;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(final String extra) {
        this.extra = extra;
    }

    public boolean isHideServer() {
        return hideServer;
    }

    public void setHideServer(final boolean hideServer) {
        this.hideServer = hideServer;
    }
}
