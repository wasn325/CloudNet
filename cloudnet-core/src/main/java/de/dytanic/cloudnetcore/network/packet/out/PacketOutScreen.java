/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;

public class PacketOutScreen extends Packet {

    public PacketOutScreen(final ServerInfo serverInfo, final DefaultType type, final boolean enable) {
        super(PacketRC.CN_CORE + 6, new Document("serverInfo", serverInfo).append("type", type).append("enable", enable));
    }

    public PacketOutScreen(final ProxyInfo serverInfo, final DefaultType type, final boolean enable) {
        super(PacketRC.CN_CORE + 6, new Document("proxyInfo", serverInfo).append("type", type).append("enable", enable));
    }
}
