/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.components;

import de.dytanic.cloudnet.lib.network.ChannelUser;
import de.dytanic.cloudnet.lib.network.protocol.IProtocol;
import de.dytanic.cloudnet.lib.network.protocol.ProtocolRequest;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnetcore.CloudNet;
import io.netty.channel.ChannelFutureListener;

/**
 * Created by Tareko on 27.05.2017.
 */
public interface INetworkComponent extends PacketSender, ChannelUser {

    Wrapper getWrapper();

    default void sendPacket(final Packet... packets) {
        for (final Packet packet : packets) {
            sendPacket(packet);
        }
    }

    String getServerId();

    default void sendPacket(final Packet packet) {
        CloudNet.getLogger().debug(
            "Sending Packet " + packet.getClass().getSimpleName() + " (id=" + CloudNet.getInstance().getPacketManager().packetId(packet) +
            ";dataLength=" + CloudNet.getInstance().getPacketManager().packetData(packet).size() + ") to " + getServerId());

        if (getChannel() == null) {
            return;
        }
        if (getChannel().eventLoop().inEventLoop()) {
            try {
                getChannel().writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            } catch (final Exception ignored) {
            }
        } else {
            getChannel().eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        getChannel().writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                    } catch (final Exception ignored) {
                    }
                }
            });
        }
    }

    default void sendPacketSynchronized(final Packet packet) {
        if (getChannel() == null) {
            return;
        }
        CloudNet.getLogger().debug(
            "Sending Packet " + packet.getClass().getSimpleName() + " (id=" + CloudNet.getInstance().getPacketManager().packetId(packet) +
            ";dataLength=" + CloudNet.getInstance().getPacketManager().packetData(packet).size() + ") to " + getServerId());
        getChannel().writeAndFlush(packet).syncUninterruptibly();
    }

    @Override
    default void send(final Object object) {
        if (getChannel() == null) {
            return;
        }

        if (getChannel().eventLoop().inEventLoop()) {
            getChannel().writeAndFlush(object);
        } else {
            getChannel().eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    getChannel().writeAndFlush(object);
                }
            });
        }
    }

    @Override
    default void sendSynchronized(final Object object) {
        getChannel().writeAndFlush(object).syncUninterruptibly();
    }

    @Override
    default void sendAsynchronized(final Object object) {
        getChannel().writeAndFlush(object);
    }

    @Override
    default void send(final IProtocol iProtocol, final Object element) {
        send(new ProtocolRequest(iProtocol.getId(), element));
    }

    @Override
    default void send(final int id, final Object element) {
        send(new ProtocolRequest(id, element));
    }

    @Override
    default void sendAsynchronized(final int id, final Object element) {
        sendAsynchronized(new ProtocolRequest(id, element));
    }

    @Override
    default void sendAsynchronized(final IProtocol iProtocol, final Object element) {
        sendAsynchronized(new ProtocolRequest(iProtocol.getId(), element));
    }

    @Override
    default void sendSynchronized(final int id, final Object element) {
        sendSynchronized(new ProtocolRequest(id, element));
    }

    @Override
    default void sendSynchronized(final IProtocol iProtocol, final Object element) {
        sendSynchronized(new ProtocolRequest(iProtocol.getId(), element));
    }
}
