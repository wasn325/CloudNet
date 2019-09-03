/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network;

import de.dytanic.cloudnet.lib.network.protocol.IProtocol;
import de.dytanic.cloudnet.lib.network.protocol.ProtocolRequest;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnetcore.CloudNet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by Tareko on 02.06.2017.
 */
public class CloudNetClientAuth extends SimpleChannelInboundHandler<Packet> implements PacketSender {

    private final Channel channel;
    private final CloudNetServer cloudNetProxyServer;

    public CloudNetClientAuth(final Channel channel, final CloudNetServer cloudNetProxyServer) {
        this.channel = channel;
        this.cloudNetProxyServer = cloudNetProxyServer;
    }

    public CloudNetServer getCloudNetProxyServer() {
        return cloudNetProxyServer;
    }

    @Override
    public String getName() {
        return "Unknown-Connection";
    }

    @Override
    public void sendPacket(final Packet... packets) {
        if (channel != null) {
            for (final Packet packet : packets) {
                sendPacket(packet);
            }
        }
    }

    @Override
    public void sendPacket(final Packet packet) {
        if (channel != null) {
            if (channel.eventLoop().inEventLoop()) {
                channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            } else {
                channel.eventLoop().execute(new Runnable() {
                    @Override
                    public void run() {
                        channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                    }
                });
            }
        }
    }

    @Override
    public void sendPacketSynchronized(final Packet packet) {
        channel.writeAndFlush(packet).syncUninterruptibly();
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        if ((!channel.isActive() || !channel.isOpen() || !channel.isWritable())) {
            channel.close().syncUninterruptibly();
        }
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final Packet packet) throws Exception {
        CloudNet.getLogger().debug("Receiving Packet [" + CloudNet.getInstance()
                                                                  .getPacketManager()
                                                                  .packetId(packet) + "] on " + getChannel().remoteAddress().toString());
        if (CloudNet.getInstance().getPacketManager().packetId(packet) == (PacketRC.INTERNAL - 1)) {
            CloudNet.getInstance().getPacketManager().dispatchPacket(packet, this);
        }
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public void send(final Object object) {
        if (channel == null) {
            return;
        }

        if (channel.eventLoop().inEventLoop()) {
            channel.writeAndFlush(object);
        } else {
            channel.eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    channel.writeAndFlush(object);
                }
            });
        }
    }

    @Override
    public void sendSynchronized(final Object object) {
        channel.writeAndFlush(object).syncUninterruptibly();
    }

    @Override
    public void sendAsynchronized(final Object object) {
        channel.writeAndFlush(object);
    }

    @Override
    public void send(final IProtocol iProtocol, final Object element) {
        send(new ProtocolRequest(iProtocol.getId(), element));
    }

    @Override
    public void send(final int id, final Object element) {
        send(new ProtocolRequest(id, element));
    }

    @Override
    public void sendAsynchronized(final int id, final Object element) {
        sendAsynchronized(new ProtocolRequest(id, element));
    }

    @Override
    public void sendAsynchronized(final IProtocol iProtocol, final Object element) {
        sendAsynchronized(new ProtocolRequest(iProtocol.getId(), element));
    }

    @Override
    public void sendSynchronized(final int id, final Object element) {
        sendSynchronized(new ProtocolRequest(id, element));
    }

    @Override
    public void sendSynchronized(final IProtocol iProtocol, final Object element) {
        sendSynchronized(new ProtocolRequest(iProtocol.getId(), element));
    }

}
