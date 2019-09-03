package de.dytanic.cloudnetcore.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.database.StatisticManager;

public final class PacketAPIInGetStatistic extends PacketAPIIO {

    @Override
    public void handleInput(final Document data, final PacketSender packetSender) {
        final Packet packet = getResult(StatisticManager.getInstance().getStatistics());
        packetSender.sendPacket(packet);
    }

    @Override
    protected Packet getResult(final Document value) {
        return new Packet(packetUniqueId, -643, value);
    }

}
