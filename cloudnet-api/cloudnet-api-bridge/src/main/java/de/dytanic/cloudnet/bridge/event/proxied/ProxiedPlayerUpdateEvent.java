/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.player.CloudPlayer;

/**
 * Calls if a online player was updated on network
 */
public class ProxiedPlayerUpdateEvent extends ProxiedCloudEvent {

    private final CloudPlayer cloudPlayer;

    public ProxiedPlayerUpdateEvent(final CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }
}
