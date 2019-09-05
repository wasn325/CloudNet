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

public class PacketOutExecuteServerCommand extends Packet {
    public PacketOutExecuteServerCommand(final ServerInfo serverInfo, final String commandLine) {
        super(PacketRC.CN_CORE + 7, new Document("serverInfo", serverInfo).append("type", DefaultType.BUKKIT)
            .append("commandLine", commandLine));
    }

    public PacketOutExecuteServerCommand(final ProxyInfo serverInfo, final String commandLine) {
        super(PacketRC.CN_CORE + 7, new Document("proxyInfo", serverInfo).append("type", DefaultType.BUNGEE_CORD)
            .append("commandLine", commandLine));
    }
}
