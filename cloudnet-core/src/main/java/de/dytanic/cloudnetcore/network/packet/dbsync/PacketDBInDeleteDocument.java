/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.dbsync;

import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

/**
 * Created by Tareko on 25.08.2017.
 */
public class PacketDBInDeleteDocument extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        String name = data.getString("name");
        CloudNet.getInstance().getDatabaseManager().getDatabase(data.getString("db")).delete(name);
    }
}
