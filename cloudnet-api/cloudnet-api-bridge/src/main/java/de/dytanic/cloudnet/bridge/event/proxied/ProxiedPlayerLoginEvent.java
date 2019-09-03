/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.player.CloudPlayer;

/**
 * Calls if a player login into the network successfully
 */
public class ProxiedPlayerLoginEvent extends ProxiedCloudEvent {

    private final CloudPlayer cloudPlayer;

    public ProxiedPlayerLoginEvent(final CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }
}
