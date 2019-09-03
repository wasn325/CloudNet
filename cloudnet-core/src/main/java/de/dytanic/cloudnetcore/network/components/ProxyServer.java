/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.components;

import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.network.NetworkInfo;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutCustomChannelMessage;
import io.netty.channel.Channel;

/**
 * Created by Tareko on 26.05.2017.
 */
public class ProxyServer implements INetworkComponent {

    private final ServiceId serviceId;
    private final Wrapper wrapper;
    private final NetworkInfo networkInfo;
    private final ProxyProcessMeta processMeta;

    private Channel channel;
    private ProxyInfo proxyInfo;
    private ProxyInfo lastProxyInfo;
    private long channelLostTime;

    public ProxyServer(final ProxyProcessMeta processMeta, final Wrapper wrapper, final ProxyInfo proxyInfo) {
        this.processMeta = processMeta;
        this.wrapper = wrapper;
        this.serviceId = proxyInfo.getServiceId();

        this.networkInfo = new NetworkInfo(proxyInfo.getServiceId().getServerId(), proxyInfo.getHost(), proxyInfo.getPort());

        this.proxyInfo = proxyInfo;
        this.lastProxyInfo = proxyInfo;
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

    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }

    public void setProxyInfo(final ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
    }

    public long getChannelLostTime() {
        return channelLostTime;
    }

    public void setChannelLostTime(final long channelLostTime) {
        this.channelLostTime = channelLostTime;
    }

    public NetworkInfo getNetworkInfo() {
        return networkInfo;
    }

    public ProxyInfo getLastProxyInfo() {
        return lastProxyInfo;
    }

    public void setLastProxyInfo(final ProxyInfo lastProxyInfo) {
        this.lastProxyInfo = lastProxyInfo;
    }

    public ProxyProcessMeta getProcessMeta() {
        return processMeta;
    }

    public void disconnect() {
        if (this.channel != null) {
            this.channel.close().syncUninterruptibly();
        }
    }

    public void sendCustomMessage(final String channel, final String message, final Document value) {
        this.sendPacket(new PacketOutCustomChannelMessage(channel, message, value));
    }

    @Override
    public String getName() {
        return serviceId.getServerId();
    }

    @Override
    public Wrapper getWrapper() {
        return wrapper;
    }

    @Override
    public String getServerId() {
        return serviceId.getServerId();
    }
}
