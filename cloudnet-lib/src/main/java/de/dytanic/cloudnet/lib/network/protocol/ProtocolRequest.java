/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol;

/**
 * Created by Tareko on 09.09.2017.
 */
public class ProtocolRequest {

    private final int id;

    private final Object element;

    public ProtocolRequest(final int id, final Object element) {
        this.id = id;
        this.element = element;
    }

    public int getId() {
        return id;
    }

    public Object getElement() {
        return element;
    }
}
