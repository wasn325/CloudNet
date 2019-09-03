/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.proxylayout;

public class TabList {

    private final boolean enabled;

    private final String header;

    private final String footer;

    public TabList(final boolean enabled, final String header, final String footer) {
        this.enabled = enabled;
        this.header = header;
        this.footer = footer;
    }

    public String getFooter() {
        return footer;
    }

    public String getHeader() {
        return header;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
