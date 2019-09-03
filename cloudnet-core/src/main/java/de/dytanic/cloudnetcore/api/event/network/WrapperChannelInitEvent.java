/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.network;

import de.dytanic.cloudnet.event.Event;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import io.netty.channel.Channel;

/**
 * Call if a wrapper is connected
 */
public class WrapperChannelInitEvent extends Event {

    private final Wrapper wrapper;

    private final Channel channel;

    public WrapperChannelInitEvent(final Wrapper wrapper, final Channel channel) {
        this.wrapper = wrapper;
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public Wrapper getWrapper() {
        return wrapper;
    }
}
