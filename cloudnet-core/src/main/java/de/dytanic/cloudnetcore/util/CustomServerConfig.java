/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.util;

/**
 * Created by Tareko on 22.08.2017.
 */
public class CustomServerConfig {

    private final String serverId;

    private final int memory;

    private final String group;
    private final String wrapper;

    private final boolean onlineMode;

    public CustomServerConfig(final String serverId, final int memory, final String group, final String wrapper, final boolean onlineMode) {
        this.serverId = serverId;
        this.memory = memory;
        this.group = group;
        this.wrapper = wrapper;
        this.onlineMode = onlineMode;
    }

    public int getMemory() {
        return memory;
    }

    public String getServerId() {
        return serverId;
    }

    public String getWrapper() {
        return wrapper;
    }

    public String getGroup() {
        return group;
    }
}
