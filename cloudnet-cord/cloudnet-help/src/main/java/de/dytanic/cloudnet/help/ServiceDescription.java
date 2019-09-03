/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.help;

/**
 * Class to store a brief and a detailed usage description.
 */
public class ServiceDescription {

    /**
     * Brief usage description.
     */
    private final String usage;

    /**
     * Detailed description.
     */
    private final String description;

    public ServiceDescription(final String usage, final String description) {
        this.usage = usage;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }
}
