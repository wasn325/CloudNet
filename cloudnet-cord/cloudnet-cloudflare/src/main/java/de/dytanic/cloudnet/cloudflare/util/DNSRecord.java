/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare.util;

import com.google.gson.JsonObject;

/**
 * General container for storing a DNS record.
 */
public class DNSRecord {

    /**
     * The type of this record.
     */
    private final String type;

    /**
     * Name of this record like in a zone file
     */
    private final String name;

    /**
     * The content of this record like in a zone file
     */
    private final String content;

    /**
     * The "Time-to-live" for this record
     */
    private final int ttl;

    /**
     * Whether the record should be proxied by CLoudFlare
     */
    private final boolean proxied;

    /**
     * Additional data about this record for SRV records
     */
    private final JsonObject data;

    public DNSRecord(final String type,
                     final String name,
                     final String content,
                     final int ttl,
                     final boolean proxied,
                     final JsonObject data) {
        this.type = type;
        this.name = name;
        this.content = content;
        this.ttl = ttl;
        this.proxied = proxied;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public int getTtl() {
        return ttl;
    }

    public JsonObject getData() {
        return data;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public boolean isProxied() {
        return proxied;
    }
}
