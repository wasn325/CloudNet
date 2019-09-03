/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Calls if a custom channel message was received
 */
public class ProxiedCustomChannelMessageReceiveEvent extends ProxiedCloudEvent {

    private final String channel;

    private final String message;

    private final Document document;

    public ProxiedCustomChannelMessageReceiveEvent(final String channel, final String message, final Document document) {
        this.channel = channel;
        this.message = message;
        this.document = document;
    }

    public String getChannel() {
        return channel;
    }

    public Document getDocument() {
        return document;
    }

    public String getMessage() {
        return message;
    }
}
