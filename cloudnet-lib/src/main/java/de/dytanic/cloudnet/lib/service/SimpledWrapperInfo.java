/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.service;

import de.dytanic.cloudnet.lib.interfaces.Nameable;

/**
 * Created by Tareko on 13.09.2017.
 */
public class SimpledWrapperInfo implements Nameable {

    private final String name;

    private final String hostName;

    public SimpledWrapperInfo(final String name, final String hostName) {
        this.name = name;
        this.hostName = hostName;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getHostName() {
        return hostName;
    }
}
