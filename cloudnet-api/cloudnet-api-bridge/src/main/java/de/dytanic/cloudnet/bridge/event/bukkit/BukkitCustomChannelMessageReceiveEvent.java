/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.lib.utility.document.Document;
import org.bukkit.event.HandlerList;

/**
 * Called if a custom channel message was received
 */
public class BukkitCustomChannelMessageReceiveEvent extends BukkitCloudEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final String channel;

    private final String message;

    private final Document document;

    public BukkitCustomChannelMessageReceiveEvent(final String channel, final String message, final Document document) {
        this.channel = channel;
        this.message = message;
        this.document = document;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public String getMessage() {
        return message;
    }

    public Document getDocument() {
        return document;
    }

    public String getChannel() {
        return channel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
