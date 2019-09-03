/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol.packet.result;

import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.UUID;

/**
 * Created by Tareko on 26.07.2017.
 */
public class Result {

    private final UUID uniqueId;

    private final Document result;

    public Result(final UUID uniqueId, final Document result) {
        this.uniqueId = uniqueId;
        this.result = result;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public Document getResult() {
        return result;
    }
}
