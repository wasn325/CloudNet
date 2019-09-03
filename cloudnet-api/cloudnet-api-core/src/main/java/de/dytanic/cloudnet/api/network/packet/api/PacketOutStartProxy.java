/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.api;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.Collection;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketOutStartProxy extends Packet {

    public PacketOutStartProxy(final ProxyGroup proxyGroup,
                               final int memory,
                               final String[] paramters,
                               final String url,
                               final Collection<ServerInstallablePlugin> plugins,
                               final Document document) {
        super(PacketRC.SERVER_HANDLE + 6, new Document("group", proxyGroup.getName()).append("memory", memory).append("url", url).append(
            "processParameters",
            paramters).append("plugins", plugins).append("properties", document));
    }

    public PacketOutStartProxy(final String wrapper,
                               final ProxyGroup proxyGroup,
                               final int memory,
                               final String[] paramters,
                               final String url,
                               final Collection<ServerInstallablePlugin> plugins,
                               final Document document) {
        super(PacketRC.SERVER_HANDLE + 6, new Document("group", proxyGroup.getName()).append("wrapper", wrapper)
                                                                                     .append("memory", memory)
                                                                                     .append("url", url)
                                                                                     .append("processParameters", paramters)
                                                                                     .append("plugins", plugins)
                                                                                     .append("properties", document));
    }

}
