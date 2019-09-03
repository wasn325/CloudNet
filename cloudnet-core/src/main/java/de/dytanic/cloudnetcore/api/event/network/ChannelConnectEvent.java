/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.network;

import de.dytanic.cloudnet.event.Cancelable;
import de.dytanic.cloudnet.event.Event;
import io.netty.channel.Channel;

/**
 * Calls if a channel is connected
 */
public class ChannelConnectEvent extends Event implements Cancelable {

    private final Channel channel;
    private boolean cancelled;

    public ChannelConnectEvent(final boolean cancelled, final Channel channel) {
        this.cancelled = cancelled;
        this.channel = channel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }

    public Channel getChannel() {
        return channel;
    }
}
