/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.wrapper;

import de.dytanic.cloudnetcore.network.components.WrapperMeta;

import java.util.UUID;

/**
 * Created by Tareko on 23.09.2017.
 */
public class WrapperSession {

    private final UUID uniqueId;

    private final WrapperMeta wrapperMeta;

    private final long connected;

    public WrapperSession(final UUID uniqueId, final WrapperMeta wrapperMeta, final long connected) {
        this.uniqueId = uniqueId;
        this.wrapperMeta = wrapperMeta;
        this.connected = connected;
    }

    public long getConnected() {
        return connected;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public WrapperMeta getWrapperMeta() {
        return wrapperMeta;
    }
}
