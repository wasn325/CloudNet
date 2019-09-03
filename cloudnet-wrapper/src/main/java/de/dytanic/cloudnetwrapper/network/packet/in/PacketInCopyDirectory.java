package de.dytanic.cloudnetwrapper.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.server.GameServer;

public final class PacketInCopyDirectory extends PacketInHandler {

    @Override
    public void handleInput(final Document data, final PacketSender packetSender) {
        if (!data.contains("directory") || !data.contains("serverInfo")) {
            return;
        }

        final ServerInfo serverInfo = data.getObject("serverInfo", ServerInfo.TYPE);
        final GameServer gameServer = CloudNetWrapper.getInstance().getServers().get(serverInfo.getServiceId().getServerId());

        if (gameServer == null) {
            return;
        }

        if (gameServer.getServerProcess().getMeta().getTemplate().getBackend() == TemplateResource.LOCAL) {
            gameServer.copyDirectory(data.getString("directory"));
        }

    }
}
