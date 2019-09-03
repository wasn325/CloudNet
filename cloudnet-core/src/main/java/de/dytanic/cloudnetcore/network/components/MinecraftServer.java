/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.components;

import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnet.lib.server.ServerProcessMeta;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutCustomSubChannelMessage;
import io.netty.channel.Channel;

/**
 * Created by Tareko on 26.05.2017.
 */
public final class MinecraftServer implements INetworkComponent {

    private final ServiceId serviceId;
    private final ServerProcessMeta processMeta;
    private final Wrapper wrapper;
    private final ServerGroupMode groupMode;

    private long channelLostTime;

    private ServerInfo serverInfo;
    private ServerInfo lastServerInfo;
    private Channel channel;

    public MinecraftServer(final ServerProcessMeta processMeta,
                           final Wrapper wrapper,
                           final ServerGroup group,
                           final ServerInfo serverInfo) {
        this.processMeta = processMeta;
        this.serviceId = serverInfo.getServiceId();
        this.wrapper = wrapper;
        this.groupMode = group.getGroupMode();

        this.serverInfo = serverInfo;
        this.lastServerInfo = serverInfo;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public void setChannel(final Channel channel) {
        this.channel = channel;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public long getChannelLostTime() {
        return channelLostTime;
    }

    public void setChannelLostTime(final long channelLostTime) {
        this.channelLostTime = channelLostTime;
    }

    public ServerInfo getLastServerInfo() {
        return lastServerInfo;
    }

    public void setLastServerInfo(final ServerInfo lastServerInfo) {
        this.lastServerInfo = lastServerInfo;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(final ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public ServerGroupMode getGroupMode() {
        return groupMode;
    }

    public ServerProcessMeta getProcessMeta() {
        return processMeta;
    }

    public void disconnect() {
        if (this.channel != null) {
            this.channel.close().syncUninterruptibly();
        }
    }

    public void sendCustomMessage(final String channel, final String message, final Document value) {
        this.sendPacket(new PacketOutCustomSubChannelMessage(channel, message, value));
    }

    public ServerGroup getGroup() {
        return CloudNet.getInstance().getServerGroup(serviceId.getGroup());
    }

    @Override
    public Wrapper getWrapper() {
        return wrapper;
    }

    @Override
    public String getServerId() {
        return serviceId.getServerId();
    }

    @Override
    public String getName() {
        return serviceId.getServerId();
    }
}
