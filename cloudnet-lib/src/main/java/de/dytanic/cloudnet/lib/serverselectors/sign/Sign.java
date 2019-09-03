package de.dytanic.cloudnet.lib.serverselectors.sign;

import de.dytanic.cloudnet.lib.server.info.ServerInfo;

import java.util.UUID;

/**
 * Created by Tareko on 03.06.2017.
 */
public class Sign {

    private final UUID uniqueId;
    private final String targetGroup;
    private final Position position;

    private volatile ServerInfo serverInfo;

    public Sign(final String targetGroup, final Position signPosition) {
        this.uniqueId = UUID.randomUUID();
        this.targetGroup = targetGroup;
        this.position = signPosition;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(final ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public Position getPosition() {
        return position;
    }

    public String getTargetGroup() {
        return targetGroup;
    }
}
