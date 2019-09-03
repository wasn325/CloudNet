/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.proxylayout;

public class Motd {

    private final String firstLine;

    private final String secondLine;

    public Motd(final String firstLine, final String secondLine) {
        this.firstLine = firstLine;
        this.secondLine = secondLine;
    }

    public String getFirstLine() {
        return firstLine;
    }

    public String getSecondLine() {
        return secondLine;
    }
}
