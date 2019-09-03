/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.network.auth.Auth;
import de.dytanic.cloudnet.lib.network.auth.AuthLoginResult;
import de.dytanic.cloudnet.lib.network.auth.AuthType;
import de.dytanic.cloudnet.lib.network.auth.packetio.PacketInAuthReader;
import de.dytanic.cloudnet.lib.network.auth.packetio.PacketOutAuthResult;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.CloudNetClient;
import de.dytanic.cloudnetcore.network.CloudNetClientAuth;
import de.dytanic.cloudnetcore.network.components.CloudServer;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import io.netty.channel.Channel;

/**
 * Created by Tareko on 25.07.2017.
 */
public final class PacketInAuthHandler extends PacketInAuthReader {

    @Override
    public void handleAuth(final Auth auth, final AuthType authType, final Document authData, final PacketSender packetSender) {
        if (!(packetSender instanceof CloudNetClientAuth)) {
            return;
        }
        final CloudNetClientAuth client = (CloudNetClientAuth) packetSender;
        switch (authType) {
            case CLOUD_NET: {
                final String key = authData.getString("key");
                final String id = authData.getString("id");

                if (CloudNet.getInstance().getWrappers().containsKey(id)) {
                    final Wrapper cn = CloudNet.getInstance().getWrappers().get(id);
                    final String wrapperKey = CloudNet.getInstance().getConfig().getWrapperKey();
                    if (wrapperKey != null && cn.getChannel() == null && wrapperKey.equals(key)) {
                        final Channel channel = client.getChannel();
                        channel.pipeline().remove("client");
                        client.getChannel().writeAndFlush(new PacketOutAuthResult(new AuthLoginResult(true))).syncUninterruptibly();
                        channel.pipeline().addLast(new CloudNetClient(cn, channel));
                        return;
                    } else {
                        client.getChannel().writeAndFlush(new PacketOutAuthResult(new AuthLoginResult(false))).syncUninterruptibly();
                        CloudNet.getLogger().info("Authentication failed [" + (wrapperKey != null
                                                                               ? "Invalid WrapperKey or Wrapper is already connected!"
                                                                               : "WrapperKey not found, please copy a wrapper key to this instance") +
                                                  ']');
                    }
                } else {
                    client.getChannel().writeAndFlush(new PacketOutAuthResult(new AuthLoginResult(false))).syncUninterruptibly();
                }
            }
            return;
            case GAMESERVER_OR_BUNGEE: {
                final ServiceId serviceId = authData.getObject("serviceId", ServiceId.class);
                if (CloudNet.getInstance().getWrappers().containsKey(serviceId.getWrapperId())) {

                    final Wrapper wrapper = CloudNet.getInstance().getWrappers().get(serviceId.getWrapperId());
                    if (wrapper.getServers().containsKey(serviceId.getServerId())) {
                        final MinecraftServer minecraftServer = wrapper.getServers().get(serviceId.getServerId());
                        if (minecraftServer.getChannel() == null && minecraftServer.getServerInfo().getServiceId().getUniqueId().equals(
                            serviceId.getUniqueId())) {
                            final Channel channel = client.getChannel();
                            channel.pipeline().remove("client");
                            channel.pipeline().addLast(new CloudNetClient(minecraftServer, channel));
                            return;
                        }
                    } else if (wrapper.getCloudServers().containsKey(serviceId.getServerId())) {
                        final CloudServer minecraftServer = wrapper.getCloudServers().get(serviceId.getServerId());
                        if (minecraftServer.getChannel() == null && minecraftServer.getServerInfo().getServiceId().getUniqueId().equals(
                            serviceId.getUniqueId())) {
                            final Channel channel = client.getChannel();
                            channel.pipeline().remove("client");
                            channel.pipeline().addLast(new CloudNetClient(minecraftServer, channel));
                            return;
                        }
                    } else if (wrapper.getProxys().containsKey(serviceId.getServerId())) {
                        final ProxyServer minecraftServer = wrapper.getProxys().get(serviceId.getServerId());
                        if (minecraftServer.getChannel() == null && minecraftServer.getProxyInfo().getServiceId().getUniqueId().equals(
                            serviceId.getUniqueId())) {
                            final Channel channel = client.getChannel();
                            channel.pipeline().remove("client");
                            channel.pipeline().addLast(new CloudNetClient(minecraftServer, channel));
                            return;
                        }
                    } else {
                        client.getChannel().close().syncUninterruptibly();
                    }
                } else {
                    client.getChannel().close().syncUninterruptibly();
                }
            }
            return;
            default:
                return;
        }
    }

}
