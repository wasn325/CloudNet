/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network;

import de.dytanic.cloudnet.lib.network.protocol.file.FileDeploy;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.scheduler.TaskScheduler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;

public class NetDispatcher extends SimpleChannelInboundHandler<Packet> {

    private final NetworkConnection networkConnection;

    private final boolean shutdownOnInactive;

    public NetDispatcher(final NetworkConnection networkConnection, final boolean shutdownOnInactive) {
        this.networkConnection = networkConnection;
        this.shutdownOnInactive = shutdownOnInactive;
    }

    public NetworkConnection getNetworkConnection() {
        return networkConnection;
    }

    public boolean isShutdownOnInactive() {
        return shutdownOnInactive;
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        if ((!ctx.channel().isActive() || !ctx.channel().isOpen() || !ctx.channel().isWritable())) {
            networkConnection.setChannel(null);
            ctx.channel().close().syncUninterruptibly();
            if (networkConnection.getTask() != null) {
                networkConnection.getTask().run();
            }
            if (shutdownOnInactive) {
                System.exit(0);
            }
        }
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        if (!(cause instanceof IOException)) {
            cause.printStackTrace();
        }
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final Packet o) throws Exception {
        if (o instanceof FileDeploy) {
            final FileDeploy deploy = ((FileDeploy) o);
            TaskScheduler.runtimeScheduler().schedule(deploy::toWrite);
        } else if (o != null) {
            TaskScheduler.runtimeScheduler().schedule(() -> {
                networkConnection.getPacketManager().dispatchPacket(o, networkConnection);
            });
        }
    }
}
