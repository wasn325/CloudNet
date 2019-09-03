/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol.packet;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.protocol.ProtocolBuffer;
import de.dytanic.cloudnet.lib.network.protocol.ProtocolStream;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * Packet objective
 */
public class Packet extends ProtocolStream {

    private static final Type TYPE = new TypeToken<Packet>() {}.getType();

    protected int id;
    protected Document data;
    protected UUID uniqueId;

    public Packet() {
    }

    public Packet(final int id) {
        this.id = id;
        this.data = new Document();
    }

    public Packet(final Document data) {
        this.data = data;
        this.id = 0;
    }

    public Packet(final int id, final Document data) {
        this.id = id;
        this.data = data;
    }

    public Packet(final UUID uniqueId, final int id, final Document data) {
        this.uniqueId = uniqueId;
        this.id = id;
        this.data = data;
    }

    @Override
    public void write(final ProtocolBuffer outPut) throws Exception {
        outPut.writeString(NetworkUtils.GSON.toJson(this));
    }

    @Override
    public void read(final ProtocolBuffer in) throws Exception {
        final int vx = in.readableBytes();
        if (vx != 0) {
            final String input = in.readString();
            final Packet packet = NetworkUtils.GSON.fromJson(input, TYPE);
            this.uniqueId = packet.uniqueId;
            this.data = packet.data;
            this.id = packet.id;
        }
    }
}
