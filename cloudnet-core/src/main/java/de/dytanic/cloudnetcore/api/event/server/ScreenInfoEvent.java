/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.server;

import de.dytanic.cloudnet.event.async.AsyncEvent;
import de.dytanic.cloudnet.event.async.AsyncPosterAdapter;
import de.dytanic.cloudnet.lib.server.screen.ScreenInfo;

import java.util.Collection;

/**
 * Calls if a screen lines was received by wrapper
 */
public class ScreenInfoEvent extends AsyncEvent<ScreenInfoEvent> {

    private final Collection<ScreenInfo> screenInfos;

    public ScreenInfoEvent(final Collection<ScreenInfo> screenInfos) {
        super(new AsyncPosterAdapter<>());
        this.screenInfos = screenInfos;
    }

    public Collection<ScreenInfo> getScreenInfos() {
        return screenInfos;
    }
}
