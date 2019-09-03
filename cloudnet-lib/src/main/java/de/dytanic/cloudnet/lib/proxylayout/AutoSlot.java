/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.proxylayout;

public class AutoSlot {

    private final int dynamicSlotSize;

    private final boolean enabled;

    public AutoSlot(final int dynamicSlotSize, final boolean enabled) {
        this.dynamicSlotSize = dynamicSlotSize;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getDynamicSlotSize() {
        return dynamicSlotSize;
    }
}
