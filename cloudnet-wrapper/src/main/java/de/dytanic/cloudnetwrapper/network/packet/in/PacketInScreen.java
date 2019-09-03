/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.server.BungeeCord;
import de.dytanic.cloudnetwrapper.server.GameServer;

public final class PacketInScreen extends PacketInHandler {

    @Override
    public void handleInput(final Document data, final PacketSender packetSender) {
        if (data.getObject("type", DefaultType.class) != DefaultType.BUNGEE_CORD) {
            final ServerInfo server = data.getObject("serverInfo", new TypeToken<ServerInfo>() {}.getType());
            if (CloudNetWrapper.getInstance().getServers().containsKey(server.getServiceId().getServerId())) {
                final GameServer gameServer = CloudNetWrapper.getInstance().getServers().get(server.getServiceId().getServerId());

                if (data.getBoolean("enable")) {
                    gameServer.enableScreenSystem();
                } else {
                    gameServer.disableScreenSystem();
                }
            }
        } else {
            final ProxyInfo server = data.getObject("proxyInfo", new TypeToken<ProxyInfo>() {}.getType());
            if (CloudNetWrapper.getInstance().getProxys().containsKey(server.getServiceId().getServerId())) {
                final BungeeCord bungee = CloudNetWrapper.getInstance().getProxys().get(server.getServiceId().getServerId());
                if (data.getBoolean("enable")) {
                    bungee.enableScreenSystem();
                } else {
                    bungee.disableScreenSystem();
                }
            }
        }
    }
}
